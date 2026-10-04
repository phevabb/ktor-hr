package com.hr.manager.services

import com.hr.manager.dtos.ManagerDirectorateStatsPageResponse

sealed interface ManagerDirectorateStatsResult {

    data class Success(
        val statistics:
        ManagerDirectorateStatsPageResponse
    ) : ManagerDirectorateStatsResult

    data object AccessDenied :
        ManagerDirectorateStatsResult

    data object AccountNotFound :
        ManagerDirectorateStatsResult

    data object AccountInactive :
        ManagerDirectorateStatsResult

    data object RegionNotAssigned :
        ManagerDirectorateStatsResult

    data object Failed :
        ManagerDirectorateStatsResult
}