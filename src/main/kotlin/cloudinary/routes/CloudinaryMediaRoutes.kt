package com.hr.cloudinary.routes

import com.hr.cloudinary.services.CloudinaryService
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

private val allowedImageContentTypes =
    setOf(
        "image/jpeg",
        "image/png",
        "image/webp"
    )

fun Route.cloudinaryRoutes() {

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

        var validationError: String? =
            null

        multipart.forEachPart { part ->
            try {
                if (
                    part is PartData.FileItem &&
                    part.name == "file" &&
                    imageBytes == null &&
                    validationError == null
                ) {
                    originalFileName =
                        part.originalFileName
                            ?.trim()
                            ?.takeIf {
                                it.isNotBlank()
                            }

                    contentType =
                        part.contentType
                            ?.toString()

                    if (
                        contentType !in
                        allowedImageContentTypes
                    ) {
                        validationError =
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
                        validationError =
                            "The selected profile picture is empty."

                        return@forEachPart
                    }

                    if (
                        bytes.size.toLong() >
                        MAXIMUM_PROFILE_PICTURE_SIZE
                    ) {
                        validationError =
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

        if (validationError != null) {
            return@post call.respond(
                HttpStatusCode.BadRequest,
                validationError!!
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
                CloudinaryService
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
                "Cloudinary upload failed: ${exception.message}"
            )

            call.respond(
                HttpStatusCode.InternalServerError,
                "Profile picture could not be uploaded."
            )
        }
    }

    /*
     * Delete profile picture
     *
     * The public ID is sent in the request body as plain text
     * because Cloudinary public IDs contain forward slashes.
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
                CloudinaryService
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
                    "Profile picture could not be deleted."
                )
            }
        } catch (exception: Exception) {
            println(
                "Cloudinary deletion failed: ${exception.message}"
            )

            call.respond(
                HttpStatusCode.InternalServerError,
                "Profile picture could not be deleted."
            )
        }
    }
}
