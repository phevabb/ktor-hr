package com.hr.currentgrade.dtos



import kotlinx.serialization.Serializable

@Serializable
data class CurrentGradeResponse(
    val id: Int,
    val currentGrade: String
)