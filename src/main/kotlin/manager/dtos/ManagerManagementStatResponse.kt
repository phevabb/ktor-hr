package com.hr.manager.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManagerManagementStatResponse(
    @SerialName("management_unit")
    val managementUnit: String,

    val count: Long
)