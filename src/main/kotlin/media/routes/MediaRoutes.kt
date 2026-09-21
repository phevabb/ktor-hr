package com.hr.media.routes


import com.hr.cloudinary.services.CloudinaryMediaService
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.utils.io.jvm.javaio.toInputStream

private const val MAXIMUM_PROFILE_PICTURE_SIZE =
    5L * 1024L * 1024L

private val allowedProfilePictureTypes =
    setOf(
        "image/jpeg",
        "image/png",
        "image/webp"
    )

fun Route.mediaRoutes() {

    /*
     * Upload profile picture
     *
     * POST /api/media/profile-picture
     */
    post("/profile-picture") {
        val multipart =
            call.receiveMultipart()

        var imageBytes: ByteArray? =
            null

        var originalFileName: String? =
            null

        var contentType: String? =
            null

        var uploadError: String? =
            null

        try {
            multipart.forEachPart { part ->
                try {
                    if (
                        part is PartData.FileItem &&
                        part.name == "file" &&
                        imageBytes == null &&
                        uploadError == null
                    ) {
                        originalFileName =
                            part.originalFileName
                                ?.trim()
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                                ?: "profile-picture"

                        contentType =
                            part.contentType
                                ?.toString()
                                ?.lowercase()

                        if (
                            contentType == null ||
                            contentType !in
                            allowedProfilePictureTypes
                        ) {
                            uploadError =
                                "Only JPG, JPEG, PNG, and WEBP images are allowed."

                            return@forEachPart
                        }

                        val bytes =
                            part.provider()
                                .toInputStream()
                                .use { inputStream ->
                                    inputStream.readBytes()
                                }

                        if (bytes.isEmpty()) {
                            uploadError =
                                "The selected profile picture is empty."

                            return@forEachPart
                        }

                        if (
                            bytes.size.toLong() >
                            MAXIMUM_PROFILE_PICTURE_SIZE
                        ) {
                            uploadError =
                                "Profile picture cannot exceed 5 MB."

                            return@forEachPart
                        }

                        imageBytes =
                            bytes
                    }
                } finally {
                    part.dispose()
                }
            }
        } catch (exception: Exception) {
            println(
                "Multipart processing failed: ${exception.message}"
            )

            return@post call.respond(
                HttpStatusCode.BadRequest,
                "The uploaded profile picture could not be processed."
            )
        }

        if (uploadError != null) {
            return@post call.respond(
                HttpStatusCode.BadRequest,
                uploadError!!
            )
        }

        val fileBytes =
            imageBytes
                ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    "No profile picture was provided."
                )

        try {
            val response =
                CloudinaryMediaService
                    .uploadProfilePicture(
                        fileBytes = fileBytes,
                        originalFileName =
                            originalFileName,
                        contentType =
                            contentType
                    )

            call.respond(
                HttpStatusCode.Created,
                response
            )
        } catch (exception: Exception) {
            println(
                "Cloudinary profile picture upload failed: ${exception.message}"
            )

            call.respond(
                HttpStatusCode.InternalServerError,
                "Profile picture could not be uploaded to Cloudinary."
            )
        }
    }

    /*
     * Delete profile picture
     *
     * Example:
     * DELETE /api/media/profile-picture
     *     ?publicId=hr/profile-pictures/image-id
     */
    delete("/profile-picture") {
        val publicId =
            call.request
                .queryParameters["publicId"]
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    "Cloudinary public ID is required."
                )

        try {
            val deleted =
                CloudinaryMediaService
                    .deleteProfilePicture(
                        publicId = publicId
                    )

            if (deleted) {
                call.respond(
                    HttpStatusCode.OK,
                    "Profile picture deleted successfully."
                )
            } else {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Cloudinary did not delete the profile picture."
                )
            }
        } catch (exception: Exception) {
            println(
                "Cloudinary profile picture deletion failed: ${exception.message}"
            )

            call.respond(
                HttpStatusCode.InternalServerError,
                "Profile picture could not be deleted from Cloudinary."
            )
        }
    }
}