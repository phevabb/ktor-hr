package com.hr.manager.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManagerLeaveTypeStatResponse(
    @SerialName("leave_type")
    val leaveType: String,

    val count: Long
)