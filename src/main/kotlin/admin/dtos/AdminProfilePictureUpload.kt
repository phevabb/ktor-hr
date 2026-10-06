package com.hr.admin.dtos

data class AdminProfilePictureUpload(
    val originalFileName: String?,
    val contentType: String?,
    val bytes: ByteArray
)