package com.hr.admin.services

import com.hr.account.dtos.AccountResponse

sealed interface AdminUserUpdateResult {

    data class Success(
        val account:
        AccountResponse
    ) : AdminUserUpdateResult

    data object AccountNotFound :
        AdminUserUpdateResult

    data object AuthenticatedAccountNotFound :
        AdminUserUpdateResult

    data object AuthenticatedAccountInactive :
        AdminUserUpdateResult

    data object InvalidRequest :
        AdminUserUpdateResult

    data class ValidationFailed(
        val errors:
        Map<String, List<String>>
    ) : AdminUserUpdateResult

    data object Failed :
        AdminUserUpdateResult
}