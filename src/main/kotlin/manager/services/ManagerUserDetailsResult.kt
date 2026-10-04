package com.hr.manager.services

import com.hr.account.dtos.AccountResponse

sealed interface ManagerUserDetailsResult {

    data class Success(
        val account: AccountResponse
    ) : ManagerUserDetailsResult

    data object AccessDenied :
        ManagerUserDetailsResult

    data object ManagerAccountNotFound :
        ManagerUserDetailsResult

    data object ManagerAccountInactive :
        ManagerUserDetailsResult

    data object ManagerRegionNotAssigned :
        ManagerUserDetailsResult

    data object UserNotFound :
        ManagerUserDetailsResult

    data object UserRegionNotAssigned :
        ManagerUserDetailsResult

    data object UserOutsideManagerRegion :
        ManagerUserDetailsResult

    data object Failed :
        ManagerUserDetailsResult
}