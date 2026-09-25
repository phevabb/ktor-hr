package com.hr.auth.dtos

import kotlinx.serialization.Serializable

@Serializable
data class AuthMessageResponse(
    val detail: String
)