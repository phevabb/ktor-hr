package com.hr.manager.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ManagerRegionStatsPageResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results:
    List<ManagerRegionStatResponse>
)