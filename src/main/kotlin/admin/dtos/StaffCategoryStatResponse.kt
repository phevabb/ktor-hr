package com.hr.admin.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StaffCategoryStatResponse(
    @SerialName("staff_category")
    val staffCategory: String,

    val count: Long
)