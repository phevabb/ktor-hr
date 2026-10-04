package com.hr.admin.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AgeStatResponse(
    @SerialName("age_range")
    val ageRange: String,
    val count: Long
)