package com.hr.manager.services

import com.hr.manager.dtos.ManagerAgeStatsPageResponse

sealed interface ManagerAgeStatsResult {

    data class Success(
        val statistics:
        ManagerAgeStatsPageResponse
    ) : ManagerAgeStatsResult

    data object AccessDenied :
        ManagerAgeStatsResult

    data object AccountNotFound :
        ManagerAgeStatsResult

    data object AccountInactive :
        ManagerAgeStatsResult

    data object RegionNotAssigned :
        ManagerAgeStatsResult

    data object Failed :
        ManagerAgeStatsResult
}