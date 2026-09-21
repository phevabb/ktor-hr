package com.hr.media.dtos


import kotlinx.serialization.Serializable

@Serializable
data class ProfilePictureUploadResponse(
    val url: String,
    val publicId: String,
    val originalFileName: String,
    val contentType: String?,
    val size: Long
)