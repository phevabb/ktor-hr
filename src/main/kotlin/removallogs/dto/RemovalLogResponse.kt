package com.hr.removallogs.dto

import kotlinx.serialization.Serializable

@Serializable
data class RemovalLogResponse(
    val id: Int,

    val accountId: Int,

    val removedAccountFullName: String,

    val removedAccountUserId: String?,

    val removedByAccountId: Int,

    val removedByFullName: String,

    val removedByUserId: String?,

    val removedByRole: String,

    val reason: String,

    val removedAt: String
)

@Serializable
data class PaginatedRemovalLogsResponse(
    val count: Long,

    val next: String?,

    val previous: String?,

    val currentPage: Int,

    val totalPages: Int,

    val pageSize: Int,

    val results: List<RemovalLogResponse>
)

@Serializable
data class RemovalLogErrorResponse(
    val error: String
)