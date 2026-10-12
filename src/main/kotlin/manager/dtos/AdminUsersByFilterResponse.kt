package com.hr.manager.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminUsersByFilterResponse(
    val count: Long,
    val next: String?,
    val previous: String?,
    val results:
    AdminUsersByFilterResults
)

@Serializable
data class AdminUsersByFilterResults(
    val dept: String,

    @SerialName("filter_type")
    val filterType: String?,

    val count: Long,

    val users:
    List<AdminFilteredUserResponse>
)
