package com.hr.account.dtos

import com.hr.account.dtos.AccountResponse
import kotlinx.serialization.Serializable

@Serializable
data class PaginatedAccountsResponse(
    val count: Long,

    val next: String?,

    val previous: String?,

    val results: List<AccountResponse>
)