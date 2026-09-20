package com.hr.districts.dtos



import kotlinx.serialization.Serializable

@Serializable
data class DistrictRequest(
    val district: String
)



@Serializable
data class DistrictResponse(
    val id: Int,
    val district: String
)