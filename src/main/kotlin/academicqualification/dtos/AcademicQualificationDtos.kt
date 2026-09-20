package com.hr.academicqualification.dtos


import kotlinx.serialization.Serializable

@Serializable
data class AcademicQualificationRequest(
    val name: String
)

@Serializable
data class AcademicQualificationResponse(
    val id: Int,
    val name: String
)