package com.hr.manager.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserRemovalLogResponse(
    val id: Int,
    val accountId: Int,
    val userId: String?,
    val fullName: String?,
    val reason: String,
    val removedAt: String
)