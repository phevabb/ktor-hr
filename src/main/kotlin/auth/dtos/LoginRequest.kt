package com.hr.auth.dtos


import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val userId: String,
    val password: String
)