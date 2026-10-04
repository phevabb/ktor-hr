package com.hr.admin.services

import com.hr.admin.dtos.UserFieldMetadataResponse

sealed interface UserFieldsResult {

    data class Success(
        val fields:
        List<UserFieldMetadataResponse>
    ) : UserFieldsResult

    data object AccessDenied :
        UserFieldsResult

    data object AccountNotFound :
        UserFieldsResult

    data object AccountInactive :
        UserFieldsResult

    data object Failed :
        UserFieldsResult
}