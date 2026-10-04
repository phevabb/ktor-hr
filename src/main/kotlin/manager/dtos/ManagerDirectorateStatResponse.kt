package com.hr.manager.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManagerDirectorateStatResponse(
    @SerialName("department")
    val departmentName: String,

    val count: Long
)