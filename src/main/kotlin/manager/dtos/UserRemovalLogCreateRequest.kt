package com.hr.manager.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserRemovalLogCreateRequest(
    val accountId: Int,
    val reason: String
)