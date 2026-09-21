package com.hr.nextgrade.dtos



import kotlinx.serialization.Serializable

@Serializable
data class NextGradeMessageResponse(
    val message: String
)