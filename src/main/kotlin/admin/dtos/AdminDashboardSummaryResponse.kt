package com.hr.admin.dtos



import kotlinx.serialization.Serializable

@Serializable
data class AdminDashboardSummaryResponse(
    val numOfUsers: Long,
    val numOfFemales: Long,
    val numOfMales: Long,
    val numOfStaffs: Long,
    val numOfAdmins: Long,
    val numOfManagers: Long
)
