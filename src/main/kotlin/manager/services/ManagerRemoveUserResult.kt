package com.hr.manager.services

import com.hr.manager.dtos.ManagerRemoveUserResponse

sealed interface ManagerRemoveUserResult {

    data class Success(
        val response:
        ManagerRemoveUserResponse
    ) : ManagerRemoveUserResult

    data object AccessDenied :
        ManagerRemoveUserResult

    data object ManagerAccountNotFound :
        ManagerRemoveUserResult

    data object ManagerAccountInactive :
        ManagerRemoveUserResult

    data object ManagerRegionNotAssigned :
        ManagerRemoveUserResult

    data object InvalidAccountId :
        ManagerRemoveUserResult

    data object ReasonRequired :
        ManagerRemoveUserResult

    data object ReasonTooLong :
        ManagerRemoveUserResult

    data object CannotRemoveSelf :
        ManagerRemoveUserResult

    data object UserNotFound :
        ManagerRemoveUserResult

    data object UserAlreadyInactive :
        ManagerRemoveUserResult

    data object UserRegionNotAssigned :
        ManagerRemoveUserResult

    data object UserOutsideManagerRegion :
        ManagerRemoveUserResult

    data object Failed :
        ManagerRemoveUserResult
}