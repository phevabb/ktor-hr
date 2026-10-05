package com.hr.superadmin.services

import com.hr.superadmin.dtos.AdminUsersByFilterResponse

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