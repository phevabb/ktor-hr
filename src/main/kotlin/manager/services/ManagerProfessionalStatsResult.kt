package com.hr.manager.services

import com.hr.manager.dtos.ManagerProfessionalStatsPageResponse

sealed interface ManagerProfessionalStatsResult {

    data class Success(
        val statistics:
        ManagerProfessionalStatsPageResponse
    ) : ManagerProfessionalStatsResult

    data object AccessDenied :
        ManagerProfessionalStatsResult

    data object AccountNotFound :
        ManagerProfessionalStatsResult

    data object AccountInactive :
        ManagerProfessionalStatsResult

    data object RegionNotAssigned :
        ManagerProfessionalStatsResult

    data object Failed :
        ManagerProfessionalStatsResult
}
