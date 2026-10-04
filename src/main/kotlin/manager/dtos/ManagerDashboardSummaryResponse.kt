package com.hr.manager.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManagerDashboardSummaryResponse(
    @SerialName("num_of_users")
    val numOfUsers: Long,

    @SerialName("num_of_females")
    val numOfFemales: Long,

    @SerialName("num_of_males")
    val numOfMales: Long,

    @SerialName("num_of_staffs")
    val numOfStaffs: Long,

    @SerialName("num_of_admins")
    val numOfAdmins: Long,

    @SerialName("num_of_managers")
    val numOfManagers: Long
)
