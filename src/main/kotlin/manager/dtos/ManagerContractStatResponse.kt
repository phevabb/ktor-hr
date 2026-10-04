package com.hr.manager.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManagerContractStatResponse(
    @SerialName("contract_type")
    val contractType: String,

    val count: Long
)