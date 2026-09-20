package com.hr.department.dto


import kotlinx.serialization.Serializable

@Serializable
data class DepartmentRequest(
    val departmentName: String
)




@Serializable
data class DepartmentResponse(
    val id: Int,
    val departmentName: String
)


