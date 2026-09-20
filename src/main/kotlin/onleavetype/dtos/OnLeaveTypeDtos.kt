package com.hr.onleavetype.dtos


import kotlinx.serialization.Serializable

@Serializable
data class OnLeaveTypeRequest(
    val name: String
)

@Serializable
data class OnLeaveTypeResponse(
    val id: Int,
    val name: String
)