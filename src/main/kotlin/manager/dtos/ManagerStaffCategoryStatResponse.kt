package com.hr.manager.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManagerStaffCategoryStatResponse(
    @SerialName("staff_category")
    val staffCategory: String,

    val count: Long
)