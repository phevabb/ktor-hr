package com.hr.changeofgrade.dtos



import kotlinx.serialization.Serializable

@Serializable
data class ChangeOfGradeResponse(
    val id: Int,
    val grade: String
)