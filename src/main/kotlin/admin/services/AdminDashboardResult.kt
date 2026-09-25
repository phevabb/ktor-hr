package com.hr.admin.services


import com.hr.admin.dtos.AdminDashboardSummaryResponse

sealed interface AdminDashboardResult {

    data class Success(
        val summary:
        AdminDashboardSummaryResponse
    ) : AdminDashboardResult

    data object AccessDenied :
        AdminDashboardResult

    data object AccountNotFound :
        AdminDashboardResult

    data object AccountInactive :
        AdminDashboardResult

    data object Failed :
        AdminDashboardResult
}