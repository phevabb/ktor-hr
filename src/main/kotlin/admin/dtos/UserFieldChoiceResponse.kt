package com.hr.admin.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserFieldChoiceResponse(
    val value: String,
    val label: String
)