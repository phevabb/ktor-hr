package com.hr.manager.services

import com.hr.manager.dtos.ManagerRegionStatsPageResponse

sealed interface ManagerRegionStatsResult {

    data class Success(
        val statistics:
        ManagerRegionStatsPageResponse
    ) : ManagerRegionStatsResult

    data object AccessDenied :
        ManagerRegionStatsResult

    data object AccountNotFound :
        ManagerRegionStatsResult

    data object AccountInactive :
        ManagerRegionStatsResult

    data object RegionNotAssigned :
        ManagerRegionStatsResult

    data object RegionNotFound :
        ManagerRegionStatsResult

    data object Failed :
        ManagerRegionStatsResult
}