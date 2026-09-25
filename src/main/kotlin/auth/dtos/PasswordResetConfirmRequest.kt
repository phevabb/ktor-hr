package com.hr.auth.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetConfirmRequest(
    val token: String,
    val newPassword: String,
    val confirmPassword: String
)