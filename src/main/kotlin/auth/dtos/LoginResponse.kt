package com.hr.auth.dtos



import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
    val tokenType: String = "Bearer",
    val expiresAt: String,
    val user: AuthenticatedUserResponse
)