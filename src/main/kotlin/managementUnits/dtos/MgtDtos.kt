package com.hr.managementunit.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ManagementUnitResponse(
    val id: Int,
    val managementUnitName: String
)

@Serializable
data class ManagementUnitRequest(
    val managementUnitName: String
)