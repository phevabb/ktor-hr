package com.hr.cloudinary.services

import com.hr.cloudinary.CloudinaryClient
import com.hr.cloudinary.dtos.ProfilePictureUploadResponse
import com.cloudinary.utils.ObjectUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CloudinaryMediaService {
    suspend fun uploadProfilePicture(
        fileBytes: ByteArray,
        originalFileName: String?,
        contentType: String?
    ): ProfilePictureUploadResponse {
        return withContext(Dispatchers.IO) {
            val uploadResult =
                CloudinaryClient.instance
                    .uploader()
                    .upload(
                        fileBytes,
                        ObjectUtils.asMap(
                            "resource_type",
                            "image",
                            "folder",
                            "hr/profile-pictures",
                            "use_filename",
                            true,
                            "unique_filename",
                            true,
                            "overwrite",
                            false
                        )
                    )

            val secureUrl =
                uploadResult["secure_url"]
                    ?.toString()
                    ?.trim()
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?: error(
                        "Cloudinary did not return a secure URL"
                    )

            val publicId =
                uploadResult["public_id"]
                    ?.toString()
                    ?.trim()
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?: error(
                        "Cloudinary did not return a public ID"
                    )

            println(
                "Profile picture uploaded to Cloudinary: $publicId"
            )

            ProfilePictureUploadResponse(
                url = secureUrl,
                publicId = publicId,
                originalFileName = originalFileName,
                contentType = contentType,
                size = fileBytes.size.toLong()
            )
        }
    }

    suspend fun deleteProfilePicture(
        publicId: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val result =
                CloudinaryClient.instance
                    .uploader()
                    .destroy(
                        publicId,
                        ObjectUtils.asMap(
                            "resource_type",
                            "image",
                            "invalidate",
                            true
                        )
                    )

            val operationResult =
                result["result"]
                    ?.toString()

            val deleted =
                operationResult == "ok" ||
                        operationResult ==
                        "not found"

            if (deleted) {
                println(
                    "Cloudinary profile picture removed: $publicId"
                )
            }

            deleted
        }
    }
}