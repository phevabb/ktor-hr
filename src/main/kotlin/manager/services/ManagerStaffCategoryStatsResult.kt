package com.hr.manager.services

import com.hr.manager.dtos.ManagerStaffCategoryStatsPageResponse

sealed interface ManagerStaffCategoryStatsResult {

    data class Success(
        val statistics:
        ManagerStaffCategoryStatsPageResponse
    ) : ManagerStaffCategoryStatsResult

    data object AccessDenied :
        ManagerStaffCategoryStatsResult

    data object AccountNotFound :
        ManagerStaffCategoryStatsResult

    data object AccountInactive :
        ManagerStaffCategoryStatsResult

    data object RegionNotAssigned :
        ManagerStaffCategoryStatsResult

    data object Failed :
        ManagerStaffCategoryStatsResult
}
