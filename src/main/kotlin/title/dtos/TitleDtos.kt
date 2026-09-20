package com.hr.title.dtos



import kotlinx.serialization.Serializable

@Serializable
data class TitleRequest(
    val title: String
)

@Serializable
data class TitleResponse(
    val id: Int,
    val title: String
)
