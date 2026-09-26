package com.hr.admin.services

import com.hr.admin.dtos.GenderStatsPageResponse

sealed interface GenderStatsResult {

    data class Success(
        val statistics:
        GenderStatsPageResponse
    ) : GenderStatsResult

    data object AccessDenied :
        GenderStatsResult

    data object AccountNotFound :
        GenderStatsResult

    data object AccountInactive :
        GenderStatsResult

    data object Failed :
        GenderStatsResult
}