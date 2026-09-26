package com.hr.admin.services

import com.hr.admin.dtos.StaffCategoryStatsPageResponse

sealed interface StaffCategoryStatsResult {

    data class Success(
        val statistics:
        StaffCategoryStatsPageResponse
    ) : StaffCategoryStatsResult

    data object AccessDenied :
        StaffCategoryStatsResult

    data object AccountNotFound :
        StaffCategoryStatsResult

    data object AccountInactive :
        StaffCategoryStatsResult

    data object Failed :
        StaffCategoryStatsResult
}
