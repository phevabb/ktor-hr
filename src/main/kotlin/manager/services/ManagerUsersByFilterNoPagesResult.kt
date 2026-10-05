package com.hr.manager.services

import com.hr.manager.dtos.ManagerUsersByFilterNoPagesResponse

sealed interface ManagerUsersByFilterNoPagesResult {

    data class Success(
        val response:
        ManagerUsersByFilterNoPagesResponse
    ) : ManagerUsersByFilterNoPagesResult

    data object AccessDenied :
        ManagerUsersByFilterNoPagesResult

    data object ManagerAccountNotFound :
        ManagerUsersByFilterNoPagesResult

    data object ManagerAccountInactive :
        ManagerUsersByFilterNoPagesResult

    data object ManagerRegionNotAssigned :
        ManagerUsersByFilterNoPagesResult

    data object FilterRequired :
        ManagerUsersByFilterNoPagesResult

    data object Failed :
        ManagerUsersByFilterNoPagesResult
}