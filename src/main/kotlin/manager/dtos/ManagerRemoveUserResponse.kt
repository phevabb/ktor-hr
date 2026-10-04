package com.hr.manager.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ManagerRemoveUserResponse(
    val message: String,
    val accountId: Int,
    val userId: String,
    val removedAt: String
)