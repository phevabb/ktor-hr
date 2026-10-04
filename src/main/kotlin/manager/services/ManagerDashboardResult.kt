package com.hr.manager.services

import com.hr.manager.dtos.ManagerDashboardSummaryResponse

sealed interface ManagerDashboardResult {

    data class Success(
        val summary:
        ManagerDashboardSummaryResponse
    ) : ManagerDashboardResult

    data object AccessDenied :
        ManagerDashboardResult

    data object AccountNotFound :
        ManagerDashboardResult

    data object AccountInactive :
        ManagerDashboardResult

    data object RegionNotAssigned :
        ManagerDashboardResult

    data object Failed :
        ManagerDashboardResult
}