package com.hr.admin.services

import com.hr.admin.dtos.RegionStatsPageResponse

sealed interface RegionStatsResult {

    data class Success(
        val statistics:
        RegionStatsPageResponse
    ) : RegionStatsResult

    data object AccessDenied :
        RegionStatsResult

    data object AccountNotFound :
        RegionStatsResult

    data object AccountInactive :
        RegionStatsResult

    data object Failed :
        RegionStatsResult
}