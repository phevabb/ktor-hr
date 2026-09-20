package com.hr.nextgrade.dtos



import kotlinx.serialization.Serializable

@Serializable
data class NextGradeRequest(
    val nextGrade: String
)