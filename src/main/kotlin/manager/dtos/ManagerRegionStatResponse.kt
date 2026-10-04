package com.hr.manager.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ManagerRegionStatResponse(
    val region: String,
    val count: Long
)