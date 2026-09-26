package com.hr.admin.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContractStatResponse(
    @SerialName("contract_type")
    val contractType: String,
    val count: Long
)