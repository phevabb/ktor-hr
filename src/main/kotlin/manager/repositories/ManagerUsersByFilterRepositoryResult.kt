package com.hr.manager.repositories

import com.hr.manager.dtos.ManagerFilteredUserResponse

data class ManagerUsersByFilterRepositoryResult(
    val filterType: String?,
    val totalCount: Long,
    val users: List<ManagerFilteredUserResponse>
)