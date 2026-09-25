package com.hr.staff.services

import com.hr.staff.dtos.StaffDetailResponse

sealed interface StaffResult {

    data class Success(
        val staff: StaffDetailResponse
    ) : StaffResult

    data object NotFound :
        StaffResult

    data object AccessDenied :
        StaffResult

    data object ManagerRegionMissing :
        StaffResult
}