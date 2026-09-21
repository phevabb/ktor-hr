package com.hr.cloudinary.services


import com.cloudinary.utils.ObjectUtils
import com.hr.cloudinary.CloudinaryClient
import com.hr.cloudinary.dtos.ProfilePictureUploadResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CloudinaryService {

    suspend fun uploadProfilePicture(
        fileBytes: ByteArray,
        originalFileName: String?,
        contentType: String?
    ): ProfilePictureUploadResponse {
        return withContext(
            Dispatchers.IO
        ) {
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
                        "Cloudinary did not return an image URL"
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
                "Cloudinary profile picture uploaded: $publicId"
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
        return withContext(
            Dispatchers.IO
        ) {
            val deleteResult =
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

            val result =
                deleteResult["result"]
                    ?.toString()

            val successful =
                result == "ok" ||
                        result == "not found"

            println(
                "Cloudinary delete result for $publicId: $result"
            )

            successful
        }
    }
}
