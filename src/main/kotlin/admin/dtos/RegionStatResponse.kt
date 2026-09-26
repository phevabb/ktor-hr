package com.hr.admin.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RegionStatResponse(
    val region: String,
    val count: Long
)