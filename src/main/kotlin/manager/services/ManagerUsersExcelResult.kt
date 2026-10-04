package com.hr.manager.services

import com.hr.manager.dtos.ManagerUserExcelResponse

sealed interface ManagerUsersExcelResult {

    data class Success(
        val users:
        List<ManagerUserExcelResponse>
    ) : ManagerUsersExcelResult

    data object AccessDenied :
        ManagerUsersExcelResult

    data object AccountNotFound :
        ManagerUsersExcelResult

    data object AccountInactive :
        ManagerUsersExcelResult

    data object RegionNotAssigned :
        ManagerUsersExcelResult

    data object Failed :
        ManagerUsersExcelResult
}