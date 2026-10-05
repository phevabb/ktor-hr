package com.hr.manager.repositories

import com.hr.account.dtos.AccountResponse

data class ManagerUsersByFilterRepositoryResult(
    val filterType: String?,
    val users: List<AccountResponse>
)
