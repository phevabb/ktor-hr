package com.hr.manager.repositories

import com.hr.manager.dtos.AdminFilteredUserResponse

data class AdminUsersByFilterRepositoryResult(
    val filterType: String?,
    val totalCount: Long,
    val users: List<AdminFilteredUserResponse>
)