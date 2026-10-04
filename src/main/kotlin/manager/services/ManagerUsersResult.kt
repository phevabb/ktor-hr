package com.hr.manager.services

import com.hr.manager.dtos.ManagerUsersPageResponse

sealed interface ManagerUsersResult {

    data class Success(
        val response:
        ManagerUsersPageResponse
    ) : ManagerUsersResult

    data object AccessDenied :
        ManagerUsersResult

    data object AccountNotFound :
        ManagerUsersResult

    data object AccountInactive :
        ManagerUsersResult

    data object RegionNotAssigned :
        ManagerUsersResult

    data object Failed :
        ManagerUsersResult
}