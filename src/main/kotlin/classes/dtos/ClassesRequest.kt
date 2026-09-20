package com.hr.classes.dtos

import kotlinx.serialization.Serializable



@Serializable
data class ClassesResponse(
    val id: Int,
    val classesName: String
)


@Serializable
data class ClassesRequest(
    val classesName: String
)