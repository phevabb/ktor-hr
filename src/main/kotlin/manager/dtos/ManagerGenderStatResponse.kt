package com.hr.manager.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ManagerGenderStatResponse(
    val gender: String,
    val count: Long
)