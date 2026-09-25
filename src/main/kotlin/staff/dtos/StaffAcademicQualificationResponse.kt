package com.hr.staff.dtos

import kotlinx.serialization.Serializable

@Serializable
data class StaffAcademicQualificationResponse(
    val id: Int,
    val name: String
)