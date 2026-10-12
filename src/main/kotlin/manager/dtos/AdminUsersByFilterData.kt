package com.hr.manager.dtos




import kotlinx.serialization.Serializable

@Serializable
data class AdminUsersByFilterData(
    val dept: String,
    val filterType: String,
    val users: List<AdminFilteredUserResponse>
)