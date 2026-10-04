package com.hr.manager.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManagerRemoveUserRequest(
    @SerialName("user_id")
    val accountId: Int,

    val reason: String
)
