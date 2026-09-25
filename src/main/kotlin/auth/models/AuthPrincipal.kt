package com.hr.auth.models



import io.ktor.server.auth.Principal

data class AuthPrincipal(
    val accountId: Int,
    val userId: String?,
    val role: String,
    val tokenId: String
) : Principal
