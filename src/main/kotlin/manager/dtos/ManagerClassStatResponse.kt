package com.hr.manager.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManagerClassStatResponse(
    @SerialName("class")
    val className: String,

    val count: Long
)
