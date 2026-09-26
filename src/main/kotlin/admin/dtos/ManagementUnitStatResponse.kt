package com.hr.admin.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManagementUnitStatResponse(
    @SerialName("management_unit")
    val managementUnit: String,
    val count: Long
)