package com.hr.manager.repositories

import java.time.LocalDateTime

sealed interface ManagerRemoveUserRepositoryResult {

    data class Success(
        val accountId: Int,
        val userId: String,
        val removedAt: LocalDateTime
    ) : ManagerRemoveUserRepositoryResult

    data object UserNotFound :
        ManagerRemoveUserRepositoryResult

    data object UserAlreadyInactive :
        ManagerRemoveUserRepositoryResult

    data object UserRegionNotAssigned :
        ManagerRemoveUserRepositoryResult

    data object UserOutsideManagerRegion :
        ManagerRemoveUserRepositoryResult

    data object Failed :
        ManagerRemoveUserRepositoryResult
}