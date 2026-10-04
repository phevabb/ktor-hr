package com.hr.admin.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserFieldMetadataResponse(
    @SerialName("field_name")
    val fieldName: String,

    @SerialName("field_type")
    val fieldType: String,

    val multiple: Boolean = false,

    val required: Boolean = false,

    val choices: List<UserFieldChoiceResponse>? = null,

    val items: List<UserFieldItemResponse>? = null
)