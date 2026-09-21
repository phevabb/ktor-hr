package com.hr.managerprofile.dtos

sealed interface ManagerProfileOperationResult {

    data object AccountNotFound :
        ManagerProfileOperationResult

    data object RegionNotFound :
        ManagerProfileOperationResult

    data object ManagerProfileNotFound :
        ManagerProfileOperationResult

    data object AccountAlreadyAssigned :
        ManagerProfileOperationResult

    data object Failed :
        ManagerProfileOperationResult

    data class Success(
        val managerProfile:
        ManagerProfileResponse
    ) : ManagerProfileOperationResult
}