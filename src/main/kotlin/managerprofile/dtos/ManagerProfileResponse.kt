package com.hr.managerprofile.dtos



import com.hr.account.dtos.Role
import kotlinx.serialization.Serializable

@Serializable
data class ManagerProfileResponse(
    val id: Int,

    val accountId: Int,
    val userId: String?,
    val fullName: String,
    val displayName: String,
    val role: Role?,
    val isActive: Boolean,
    val isStaff: Boolean,
    val isSuperuser: Boolean,

    val regionId: Int,
    val regionName: String
)