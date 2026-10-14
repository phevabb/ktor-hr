package com.hr.manager.dtos



import kotlinx.serialization.Serializable

@Serializable
data class ManagerFilteredUserResponse(
    val id: Int,
    val userId: String?,
    val firstName: String?,
    val fullName: String?,
    val phoneNumber: String?,
    val profilePictureUrl: String?
)