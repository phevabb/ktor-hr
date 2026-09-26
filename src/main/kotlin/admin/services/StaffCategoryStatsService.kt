package com.hr.admin.services

import com.hr.admin.dtos.StaffCategoryStatsPageResponse
import com.hr.admin.repositories.StaffCategoryStatsRepository
import com.hr.auth.repositories.AuthRepository
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

object StaffCategoryStatsService {

    suspend fun getStaffCategoryStats(
        accountId: Int,
        page: Int,
        pageSize: Int,
        requestPath: String
    ): StaffCategoryStatsResult {
        val account =
            AuthRepository
                .findAccountById(
                    accountId
                )
                ?: return StaffCategoryStatsResult
                    .AccountNotFound

        if (!account.isActive) {
            return StaffCategoryStatsResult
                .AccountInactive
        }

        val isAdmin =
            account.role.equals(
                other = "Admin",
                ignoreCase = true
            )

        if (!isAdmin) {
            println(
                "Staff category statistics access denied: " +
                        "accountId=${account.id}, " +
                        "userId=${account.userId}, " +
                        "role=${account.role}"
            )

            return StaffCategoryStatsResult
                .AccessDenied
        }

        return try {
            val allStatistics =
                StaffCategoryStatsRepository
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
                StaffCategoryStatsPageResponse(
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
                "Staff category statistics returned: " +
                        "accountId=${account.id}, " +
                        "page=$safePage, " +
                        "pageSize=$safePageSize, " +
                        "count=${response.count}, " +
                        "results=${response.results}"
            )

            StaffCategoryStatsResult.Success(
                statistics =
                    response
            )
        } catch (exception: Exception) {
            println(
                "Staff category statistics failed: " +
                        "accountId=${account.id}, " +
                        "errorType=${exception::class.simpleName}, " +
                        "message=${exception.message}"
            )

            StaffCategoryStatsResult.Failed
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