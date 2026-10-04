package com.hr.admin.services

import com.hr.admin.dtos.SalaryGradeStatsPageResponse

sealed interface SalaryGradeStatsResult {

    data class Success(
        val statistics:
        SalaryGradeStatsPageResponse
    ) : SalaryGradeStatsResult

    data object AccessDenied :
        SalaryGradeStatsResult

    data object AccountNotFound :
        SalaryGradeStatsResult

    data object AccountInactive :
        SalaryGradeStatsResult

    data object Failed :
        SalaryGradeStatsResult
}