package com.hr.admin.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ManagementUnitStatsPageResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ManagementUnitStatResponse>
)