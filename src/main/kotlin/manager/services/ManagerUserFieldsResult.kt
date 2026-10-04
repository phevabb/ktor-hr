package com.hr.manager.services

import com.hr.manager.dtos.ManagerUserFieldResponse

sealed interface ManagerUserFieldsResult {

    data class Success(
        val fields:
        List<ManagerUserFieldResponse>
    ) : ManagerUserFieldsResult

    data object AccessDenied :
        ManagerUserFieldsResult

    data object AccountNotFound :
        ManagerUserFieldsResult

    data object AccountInactive :
        ManagerUserFieldsResult

    data object RegionNotAssigned :
        ManagerUserFieldsResult

    data object Failed :
        ManagerUserFieldsResult
}