package com.hr.admin.dtos



import kotlinx.serialization.Serializable

@Serializable
data class ProfessionalStatResponse(
    val professional: String,
    val count: Long
)