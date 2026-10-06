package com.hr.admin.services

import com.hr.admin.dtos.AdminUserFieldResponse

sealed interface AdminUserFieldsResult {

    data class Success(
        val fields:
        List<AdminUserFieldResponse>
    ) : AdminUserFieldsResult

    data object AccountNotFound :
        AdminUserFieldsResult

    data object AccountInactive :
        AdminUserFieldsResult

    data object Failed :
        AdminUserFieldsResult
}