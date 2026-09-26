package com.hr.admin.services

import com.hr.admin.dtos.ManagementUnitStatsPageResponse

sealed interface ManagementUnitStatsResult {

    data class Success(
        val statistics:
        ManagementUnitStatsPageResponse
    ) : ManagementUnitStatsResult

    data object AccessDenied :
        ManagementUnitStatsResult

    data object AccountNotFound :
        ManagementUnitStatsResult

    data object AccountInactive :
        ManagementUnitStatsResult

    data object Failed :
        ManagementUnitStatsResult
}