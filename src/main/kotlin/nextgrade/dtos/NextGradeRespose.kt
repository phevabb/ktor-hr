package com.hr.nextgrade.dtos


import kotlinx.serialization.Serializable

@Serializable
data class NextGradeResponse(
    val id: Int,
    val nextGrade: String
)