package com.hr.account.dtos

import kotlinx.serialization.Serializable

@Serializable
data class AccountListItemResponse(
    val id: Int,

    val userId: String?,

    val fullName: String,

    val phoneNumber: String?,

    val email: String?,

    val role: String?,

    val gender: String?,

    val profilePictureUrl: String?,

    val isActive: Boolean,

    val regionId: Int?,

    val districtId: Int?,

    val directorateId: Int?,

    val categoryId: Int?,

    val currentGradeId: Int?,

    val managementUnitCostCentreId: Int?
)
