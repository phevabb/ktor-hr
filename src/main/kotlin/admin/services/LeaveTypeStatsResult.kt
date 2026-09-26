package com.hr.admin.services

import com.hr.admin.dtos.LeaveTypeStatsPageResponse

sealed interface LeaveTypeStatsResult {

    data class Success(
        val statistics:
        LeaveTypeStatsPageResponse
    ) : LeaveTypeStatsResult

    data object AccessDenied :
        LeaveTypeStatsResult

    data object AccountNotFound :
        LeaveTypeStatsResult

    data object AccountInactive :
        LeaveTypeStatsResult

    data object Failed :
        LeaveTypeStatsResult
}