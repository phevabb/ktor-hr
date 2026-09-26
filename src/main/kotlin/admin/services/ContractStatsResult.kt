package com.hr.admin.services

import com.hr.admin.dtos.ContractStatsPageResponse

sealed interface ContractStatsResult {

    data class Success(
        val statistics:
        ContractStatsPageResponse
    ) : ContractStatsResult

    data object AccessDenied :
        ContractStatsResult

    data object AccountNotFound :
        ContractStatsResult

    data object AccountInactive :
        ContractStatsResult

    data object Failed :
        ContractStatsResult
}