package com.hr.account.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedAccountListItemResponse(
    val count: Long,

    val next: String?,

    val previous: String?,

    val results: List<AccountListItemResponse>
)