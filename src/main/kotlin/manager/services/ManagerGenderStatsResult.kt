package com.hr.manager.services

import com.hr.manager.dtos.ManagerGenderStatsPageResponse

sealed interface ManagerGenderStatsResult {

    data class Success(
        val statistics:
        ManagerGenderStatsPageResponse
    ) : ManagerGenderStatsResult

    data object AccessDenied :
        ManagerGenderStatsResult

    data object AccountNotFound :
        ManagerGenderStatsResult

    data object AccountInactive :
        ManagerGenderStatsResult

    data object RegionNotAssigned :
        ManagerGenderStatsResult

    data object Failed :
        ManagerGenderStatsResult
}