package com.hr.currentgrade.dtos


import kotlinx.serialization.Serializable

@Serializable
data class CurrentGradeRequest(
    val currentGrade: String)