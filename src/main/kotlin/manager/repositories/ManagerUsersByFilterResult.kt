package com.hr.manager.repositories

import com.hr.manager.dtos.ManagerUsersByFilterResponse

sealed interface ManagerUsersByFilterResult {

    data class Success(
        val response:
        ManagerUsersByFilterResponse
    ) : ManagerUsersByFilterResult

    data object AccessDenied :
        ManagerUsersByFilterResult

    data object ManagerAccountNotFound :
        ManagerUsersByFilterResult

    data object ManagerAccountInactive :
        ManagerUsersByFilterResult

    data object ManagerRegionNotAssigned :
        ManagerUsersByFilterResult

    data object FilterRequired :
        ManagerUsersByFilterResult

    data object Failed :
        ManagerUsersByFilterResult
}