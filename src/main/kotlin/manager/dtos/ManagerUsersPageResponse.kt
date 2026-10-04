package com.hr.manager.dtos

import com.hr.account.dtos.AccountResponse
import kotlinx.serialization.Serializable

@Serializable
data class ManagerUsersPageResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<AccountResponse>
)