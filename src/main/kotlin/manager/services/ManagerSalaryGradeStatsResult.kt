package com.hr.manager.services

import com.hr.manager.dtos.ManagerSalaryGradeStatsPageResponse

sealed interface ManagerSalaryGradeStatsResult {

    data class Success(
        val statistics:
        ManagerSalaryGradeStatsPageResponse
    ) : ManagerSalaryGradeStatsResult

    data object AccessDenied :
        ManagerSalaryGradeStatsResult

    data object AccountNotFound :
        ManagerSalaryGradeStatsResult

    data object AccountInactive :
        ManagerSalaryGradeStatsResult

    data object RegionNotAssigned :
        ManagerSalaryGradeStatsResult

    data object Failed :
        ManagerSalaryGradeStatsResult
}