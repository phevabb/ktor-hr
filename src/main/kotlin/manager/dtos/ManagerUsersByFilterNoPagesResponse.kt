package com.hr.manager.dtos

import com.hr.account.dtos.AccountResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManagerUsersByFilterNoPagesResponse(
    val dept: String,

    @SerialName("filter_type")
    val filterType: String?,

    val count: Int,

    val users:
    List<AccountResponse>
)