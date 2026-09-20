package com.hr.staffclass.dtos


import kotlinx.serialization.Serializable

@Serializable
data class StaffClassRequest(
    val name: String
)

@Serializable
data class StaffClassResponse(
    val id: Int,
    val name: String
)