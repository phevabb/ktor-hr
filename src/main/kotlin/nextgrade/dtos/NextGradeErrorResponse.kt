package com.hr.nextgrade.dtos



import kotlinx.serialization.Serializable

@Serializable
data class NextGradeErrorResponse(
    val message: String
)