package com.hr.admin.services

import com.hr.admin.dtos.AdminProfilePictureUpload
import com.hr.cloudinary.services.CloudinaryService

object CloudinaryAdminProfilePictureStorage :
    AdminProfilePictureStorage {

    override suspend fun upload(
        accountId: Int,
        upload: AdminProfilePictureUpload
    ): StoredProfilePicture {
        println(
            "=================================================="
        )

        println(
            "Uploading Admin account profile picture"
        )

        println(
            "Account ID: $accountId"
        )

        println(
            "Original file name: ${upload.originalFileName}"
        )

        println(
            "Content type: ${upload.contentType ?: "Not provided"}"
        )

        println(
            "File size: ${upload.bytes.size} bytes"
        )

        val response =
            CloudinaryService
                .uploadProfilePicture(
                    fileBytes =
                        upload.bytes,

                    originalFileName =
                        upload.originalFileName,

                    contentType =
                        upload.contentType
                )

        println(
            "Admin account profile picture uploaded successfully"
        )

        println(
            "Account ID: $accountId"
        )

        println(
            "Cloudinary public ID: ${response.publicId}"
        )

        println(
            "Cloudinary secure URL: ${response.url}"
        )

        println(
            "=================================================="
        )

        return StoredProfilePicture(
            url =
                response.url,

            publicId =
                response.publicId
        )
    }

    override suspend fun delete(
        publicId: String
    ) {
        val normalizedPublicId =
            publicId.trim()

        if (normalizedPublicId.isBlank()) {
            println(
                "Cloudinary deletion skipped because the public ID is blank"
            )

            return
        }

        println(
            "=================================================="
        )

        println(
            "Deleting Admin account profile picture"
        )

        println(
            "Cloudinary public ID: $normalizedPublicId"
        )

        val deleted =
            CloudinaryService
                .deleteProfilePicture(
                    publicId =
                        normalizedPublicId
                )

        if (!deleted) {
            println(
                "Cloudinary profile picture deletion was unsuccessful"
            )

            println(
                "Cloudinary public ID: $normalizedPublicId"
            )

            println(
                "=================================================="
            )

            error(
                "Cloudinary profile picture could not be deleted."
            )
        }

        println(
            "Cloudinary profile picture deleted successfully"
        )

        println(
            "Cloudinary public ID: $normalizedPublicId"
        )

        println(
            "=================================================="
        )
    }
}