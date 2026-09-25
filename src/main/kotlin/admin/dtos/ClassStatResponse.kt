package com.hr.admin.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClassStatResponse(
    @SerialName("class")
    val className: String,
    val count: Long
)