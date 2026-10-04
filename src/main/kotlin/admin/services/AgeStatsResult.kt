package com.hr.admin.services

import com.hr.admin.dtos.AgeStatsPageResponse

sealed interface AgeStatsResult {

    data class Success(
        val statistics:
        AgeStatsPageResponse
    ) : AgeStatsResult

    data object AccessDenied :
        AgeStatsResult

    data object AccountNotFound :
        AgeStatsResult

    data object AccountInactive :
        AgeStatsResult

    data object Failed :
        AgeStatsResult
}