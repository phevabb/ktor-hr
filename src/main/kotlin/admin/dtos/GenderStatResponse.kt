package com.hr.admin.dtos

import kotlinx.serialization.Serializable

@Serializable
data class GenderStatResponse(
    val gender: String,
    val count: Long
)