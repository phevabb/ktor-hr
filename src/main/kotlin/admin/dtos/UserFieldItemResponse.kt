package com.hr.admin.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserFieldItemResponse(
    val id: Int,
    val name: String
)
