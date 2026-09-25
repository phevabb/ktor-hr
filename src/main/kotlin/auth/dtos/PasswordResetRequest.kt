package com.hr.auth.dtos



import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetRequest(
    val userId: String
)