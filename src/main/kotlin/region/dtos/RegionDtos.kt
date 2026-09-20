package com.hr.region.dtos



import kotlinx.serialization.Serializable


@Serializable
data class RegionRequest(
    val region: String
)

@Serializable
data class RegionResponse(
    val id: Int,
    val region: String
)
