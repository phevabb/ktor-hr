package com.hr.manager.services

import com.hr.manager.dtos.ManagerContractStatsPageResponse

sealed interface ManagerContractStatsResult {

    data class Success(
        val statistics:
        ManagerContractStatsPageResponse
    ) : ManagerContractStatsResult

    data object AccessDenied :
        ManagerContractStatsResult

    data object AccountNotFound :
        ManagerContractStatsResult

    data object AccountInactive :
        ManagerContractStatsResult

    data object RegionNotAssigned :
        ManagerContractStatsResult

    data object Failed :
        ManagerContractStatsResult
}