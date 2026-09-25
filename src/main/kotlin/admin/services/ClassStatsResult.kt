package com.hr.admin.services


import com.hr.admin.dtos.ClassStatsPageResponse

sealed interface ClassStatsResult {

    data class Success(
        val statistics:
        ClassStatsPageResponse
    ) : ClassStatsResult

    data object AccessDenied :
        ClassStatsResult

    data object AccountNotFound :
        ClassStatsResult

    data object AccountInactive :
        ClassStatsResult

    data object Failed :
        ClassStatsResult
}