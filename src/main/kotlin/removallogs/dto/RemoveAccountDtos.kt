package com.hr.removallogs.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoveAccountRequest(
    @SerialName(
        "user_id"
    )
    val userId: Int? = null,

    val reason: String? = null
)

@Serializable
data class RemoveAccountSuccessResponse(
    val message: String,

    @SerialName(
        "account_id"
    )
    val accountId: Int,

    val reason: String
)

@Serializable
data class RemoveAccountErrorResponse(
    val error: String
)