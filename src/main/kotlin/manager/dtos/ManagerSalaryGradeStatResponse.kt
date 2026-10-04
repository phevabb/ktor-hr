package com.hr.manager.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManagerSalaryGradeStatResponse(
    @SerialName("salary_range")
    val salaryRange: String,

    val count: Long
)