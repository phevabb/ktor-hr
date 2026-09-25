package com.hr.staff.dtos

import kotlinx.serialization.Serializable

@Serializable
data class StaffSummaryResponse(
    val id: Int,
    val userId: String?,
    val fullName: String,
    val displayName: String,
    val role: String?,
    val profilePictureUrl: String?,
    val phoneNumber: String?,
    val isActive: Boolean,
    val isStaff: Boolean,
    val isSuperuser: Boolean,

    val regionId: Int?,
    val regionName: String?,

    val districtId: Int?,
    val districtName: String?,

    val directorateId: Int?,
    val directorateName: String?,

    val currentGradeId: Int?,
    val currentGradeName: String?
)