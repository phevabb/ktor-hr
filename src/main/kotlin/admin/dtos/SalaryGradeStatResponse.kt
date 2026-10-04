package com.hr.admin.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SalaryGradeStatResponse(
    @SerialName("salary_range")
    val salaryRange: String,
    val count: Long
)