package com.hr.auth.dtos



import kotlinx.serialization.Serializable

@Serializable
data class AuthenticatedUserResponse(
    val id: Int,
    val userId: String?,
    val role: String,
    val fullName: String,
    val displayName: String,
    val isActive: Boolean,
    val isStaff: Boolean,
    val isSuperuser: Boolean,
    val regionId: Int? = null,
    val regionName: String? = null
)