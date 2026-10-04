package com.hr.manager.services

import com.hr.account.dtos.AccountResponse

sealed interface ManagerCreateUserResult {

    data class Success(
        val account:
        AccountResponse
    ) : ManagerCreateUserResult

    data object AccessDenied :
        ManagerCreateUserResult

    data object ManagerAccountNotFound :
        ManagerCreateUserResult

    data object ManagerAccountInactive :
        ManagerCreateUserResult

    data object ManagerRegionNotAssigned :
        ManagerCreateUserResult

    data object UserIdRequired :
        ManagerCreateUserResult

    data object UserIdExists :
        ManagerCreateUserResult

    data object PhoneNumberExists :
        ManagerCreateUserResult

    data object CreatedAccountNotFound :
        ManagerCreateUserResult

    data object Failed :
        ManagerCreateUserResult
}