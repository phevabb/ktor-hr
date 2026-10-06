package com.hr.admin.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminUserFieldResponse(
    @SerialName("field_name")
    val fieldName: String,

    @SerialName("field_type")
    val fieldType: String,

    val multiple: Boolean = false,

    val required: Boolean = false,

    val choices:
    List<AdminUserFieldChoiceResponse>? =
        null,

    val items:
    List<AdminUserFieldItemResponse>? =
        null
)

@Serializable
data class AdminUserFieldChoiceResponse(
    val value: String,
    val label: String
)

@Serializable
data class AdminUserFieldItemResponse(
    val id: Int,
    val name: String
)