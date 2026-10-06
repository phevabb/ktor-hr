package com.hr.admin.services

import com.hr.admin.dtos.AdminProfilePictureUpload

data class StoredProfilePicture(
    val url: String,
    val publicId: String?
)

interface AdminProfilePictureStorage {

    suspend fun upload(
        accountId: Int,
        upload: AdminProfilePictureUpload
    ): StoredProfilePicture

    suspend fun delete(
        publicId: String
    )
}