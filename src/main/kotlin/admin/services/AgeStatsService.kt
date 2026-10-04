package com.hr.admin.services

import com.hr.admin.dtos.AgeStatsPageResponse
import com.hr.admin.repositories.AgeStatsRepository
import com.hr.auth.repositories.AuthRepository
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

object AgeStatsService {

    suspend fun getAgeStats(
        accountId: Int,
        page: Int,
        pageSize: Int,
        requestPath: String
    ): AgeStatsResult {
        val account =
            AuthRepository
                .findAccountById(
                    accountId
                )
                ?: return AgeStatsResult
                    .AccountNotFound

        if (!account.isActive) {
            return AgeStatsResult
                .AccountInactive
        }

        val isAdmin =
            account.role.equals(
                other = "Admin",
                ignoreCase = true
            )

        if (!isAdmin) {
            println(
                "Age statistics access denied: " +
                        "accountId=${account.id}, " +
                        "userId=${account.userId}, " +
                        "role=${account.role}"
            )

            return AgeStatsResult
                .AccessDenied
        }

        return try {
            val allStatistics =
                AgeStatsRepository
                    .getAll()

            val safePageSize =
                pageSize.coerceIn(
                    minimumValue = 1,
                    maximumValue = 100
                )

            val totalRecords =
                allStatistics.size

            val totalPages =
                max(
                    1,
                    ceil(
                        totalRecords.toDouble() /
                                safePageSize.toDouble()
                    ).toInt()
                )

            val safePage =
                page.coerceIn(
                    minimumValue = 1,
                    maximumValue = totalPages
                )

            val startIndex =
                (
                        (safePage - 1) *
                                safePageSize
                        ).coerceAtMost(
                        totalRecords
                    )

            val endIndex =
                min(
                    startIndex +
                            safePageSize,
                    totalRecords
                )

            val results =
                if (
                    startIndex <
                    endIndex
                ) {
                    allStatistics.subList(
                        startIndex,
                        endIndex
                    )
                } else {
                    emptyList()
                }

            val nextPageUrl =
                if (
                    safePage <
                    totalPages
                ) {
                    buildPageUrl(
                        requestPath =
                            requestPath,

                        page =
                            safePage + 1,

                        pageSize =
                            safePageSize
                    )
                } else {
                    null
                }

            val previousPageUrl =
                if (safePage > 1) {
                    buildPageUrl(
                        requestPath =
                            requestPath,

                        page =
                            safePage - 1,

                        pageSize =
                            safePageSize
                    )
                } else {
                    null
                }

            val response =
                AgeStatsPageResponse(
                    count =
                        totalRecords,

                    next =
                        nextPageUrl,

                    previous =
                        previousPageUrl,

                    results =
                        results
                )

            println(
                "Age statistics returned: " +
                        "accountId=${account.id}, " +
                        "page=$safePage, " +
                        "pageSize=$safePageSize, " +
                        "count=${response.count}, " +
                        "results=${response.results}"
            )

            AgeStatsResult.Success(
                statistics =
                    response
            )
        } catch (exception: Exception) {
            println(
                "Age statistics failed: " +
                        "accountId=${account.id}, " +
                        "errorType=${exception::class.simpleName}, " +
                        "message=${exception.message}"
            )

            AgeStatsResult.Failed
        }
    }

    private fun buildPageUrl(
        requestPath: String,
        page: Int,
        pageSize: Int
    ): String {
        return (
                "$requestPath" +
                        "?page=$page" +
                        "&page_size=$pageSize"
                )
    }
}