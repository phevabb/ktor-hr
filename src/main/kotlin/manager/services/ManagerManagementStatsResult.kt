package com.hr.manager.services

import com.hr.manager.dtos.ManagerManagementStatsPageResponse

sealed interface ManagerManagementStatsResult {

    data class Success(
        val statistics:
        ManagerManagementStatsPageResponse
    ) : ManagerManagementStatsResult

    data object AccessDenied :
        ManagerManagementStatsResult

    data object AccountNotFound :
        ManagerManagementStatsResult

    data object AccountInactive :
        ManagerManagementStatsResult

    data object RegionNotAssigned :
        ManagerManagementStatsResult

    data object Failed :
        ManagerManagementStatsResult
}