package com.hr.admin.dtos

import kotlinx.serialization.Serializable

@Serializable
data class DepartmentStatResponse(
    val department: String,
    val count: Long
)