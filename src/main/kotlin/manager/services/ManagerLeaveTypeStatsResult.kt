package com.hr.manager.services

import com.hr.manager.dtos.ManagerLeaveTypeStatsPageResponse

sealed interface ManagerLeaveTypeStatsResult {

    data class Success(
        val statistics:
        ManagerLeaveTypeStatsPageResponse
    ) : ManagerLeaveTypeStatsResult

    data object AccessDenied :
        ManagerLeaveTypeStatsResult

    data object AccountNotFound :
        ManagerLeaveTypeStatsResult

    data object AccountInactive :
        ManagerLeaveTypeStatsResult

    data object RegionNotAssigned :
        ManagerLeaveTypeStatsResult

    data object Failed :
        ManagerLeaveTypeStatsResult
}