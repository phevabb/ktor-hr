package com.hr.position.dots


import kotlinx.serialization.Serializable

@Serializable
data class PositionRequest(
    val name: String
)

@Serializable
data class PositionResponse(
    val id: Int,
    val name: String
)