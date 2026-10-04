package com.hr.manager.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ManagerProfessionalStatResponse(
    val professional: String,
    val count: Long
)