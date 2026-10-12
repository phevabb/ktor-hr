package com.hr.manager.services

import com.hr.manager.dtos.AdminUsersByFilterResponse


sealed interface AdminUsersByFilterResult {

    data class Success(
        val response:
        AdminUsersByFilterResponse
    ) : AdminUsersByFilterResult

    data object AccountNotFound :
        AdminUsersByFilterResult

    data object AccountInactive :
        AdminUsersByFilterResult

    data object AccessDenied :
        AdminUsersByFilterResult

    data object FilterRequired :
        AdminUsersByFilterResult

    data object Failed :
        AdminUsersByFilterResult
}