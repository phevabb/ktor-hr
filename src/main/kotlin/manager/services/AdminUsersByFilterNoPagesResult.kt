package com.hr.manager.services

import com.hr.manager.dtos.AdminUsersByFilterNoPagesResponse


sealed interface AdminUsersByFilterNoPagesResult {

    data class Success(
        val response:
        AdminUsersByFilterNoPagesResponse
    ) : AdminUsersByFilterNoPagesResult

    data object AccountNotFound :
        AdminUsersByFilterNoPagesResult

    data object AccountInactive :
        AdminUsersByFilterNoPagesResult

    data object AccessDenied :
        AdminUsersByFilterNoPagesResult

    data object FilterRequired :
        AdminUsersByFilterNoPagesResult

    data object Failed :
        AdminUsersByFilterNoPagesResult
}
