package com.hr.admin.services

import com.hr.admin.dtos.DepartmentStatsPageResponse

sealed interface DepartmentStatsResult {

    data class Success(
        val statistics:
        DepartmentStatsPageResponse
    ) : DepartmentStatsResult

    data object AccessDenied :
        DepartmentStatsResult

    data object AccountNotFound :
        DepartmentStatsResult

    data object AccountInactive :
        DepartmentStatsResult

    data object Failed :
        DepartmentStatsResult
}