package com.hr.admin.services

import com.hr.admin.dtos.ProfessionalStatsPageResponse

sealed interface ProfessionalStatsResult {

    data class Success(
        val statistics:
        ProfessionalStatsPageResponse
    ) : ProfessionalStatsResult

    data object AccessDenied :
        ProfessionalStatsResult

    data object AccountNotFound :
        ProfessionalStatsResult

    data object AccountInactive :
        ProfessionalStatsResult

    data object Failed :
        ProfessionalStatsResult
}