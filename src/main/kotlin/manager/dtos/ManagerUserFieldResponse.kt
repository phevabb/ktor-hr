package com.hr.manager.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManagerUserFieldResponse(
    @SerialName("field_name")
    val fieldName: String,

    @SerialName("field_type")
    val fieldType: String,

    val multiple: Boolean =
        false,

    val required: Boolean =
        false,

    val choices:
    List<ManagerUserFieldChoiceResponse>? =
        null,

    val items:
    List<ManagerUserFieldItemResponse>? =
        null
)

@Serializable
data class ManagerUserFieldChoiceResponse(
    val value: String,
    val label: String
)

@Serializable
data class ManagerUserFieldItemResponse(
    val id: Int,
    val name: String
)