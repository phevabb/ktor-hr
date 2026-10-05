package com.hr.manager.dtos

import com.hr.account.dtos.AccountResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManagerUsersByFilterResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results:
    ManagerUsersByFilterResults
)

@Serializable
data class ManagerUsersByFilterResults(
    val dept: String,

    @SerialName("filter_type")
    val filterType: String?,

    val count: Int,

    val users:
    List<AccountResponse>
)