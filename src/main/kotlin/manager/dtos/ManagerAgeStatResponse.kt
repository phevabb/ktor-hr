package com.hr.manager.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManagerAgeStatResponse(
    @SerialName("age_range")
    val ageRange: String,

    val count: Long
)