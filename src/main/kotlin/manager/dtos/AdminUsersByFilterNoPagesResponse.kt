package com.hr.manager.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminUsersByFilterNoPagesResponse(
    val dept: String,

    @SerialName("filter_type")
    val filterType: String?,

    val count: Int,

    val users:
    List<AdminFilteredUserResponse>
)