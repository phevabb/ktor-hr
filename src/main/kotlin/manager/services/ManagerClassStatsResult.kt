package com.hr.manager.services

import com.hr.manager.dtos.ManagerClassStatsPageResponse

sealed interface ManagerClassStatsResult {

    data class Success(
        val statistics:
        ManagerClassStatsPageResponse
    ) : ManagerClassStatsResult

    data object AccessDenied :
        ManagerClassStatsResult

    data object AccountNotFound :
        ManagerClassStatsResult

    data object AccountInactive :
        ManagerClassStatsResult

    data object RegionNotAssigned :
        ManagerClassStatsResult

    data object Failed :
        ManagerClassStatsResult
}