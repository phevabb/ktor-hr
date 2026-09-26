package com.hr.admin.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaveTypeStatResponse(
    @SerialName("leave_type")
    val leaveType: String,
    val count: Long
)