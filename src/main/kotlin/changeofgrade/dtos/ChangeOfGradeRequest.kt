package com.hr.changeofgrade.dtos



import kotlinx.serialization.Serializable

@Serializable
data class ChangeOfGradeRequest(
    val grade: String
)