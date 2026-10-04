package com.hr.account.dtos

import kotlinx.serialization.Serializable

@Serializable
data class AccountAcademicQualificationResponse(
    val id: Int,
    val name: String
)