package com.hr.admin.services


import com.hr.admin.dtos.ClassStatsPageResponse
import com.hr.admin.repositories.ClassStatsRepository
import com.hr.auth.repositories.AuthRepository
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

object ClassStatsService {

    suspend fun getClassStats(
        accountId: Int,
        page: Int,
        pageSize: Int,
        requestPath: String
    ): ClassStatsResult {
        val account =
            AuthRepository
                .findAccountById(
                    accountId
                )
                ?: return ClassStatsResult
                    .AccountNotFound

        if (!account.isActive) {
            return ClassStatsResult
                .AccountInactive
        }

        val isAdmin =
            account.role.equals(
                other = "Admin",
                ignoreCase = true
            )

        if (!isAdmin) {
            println(
                "Class statistics access denied: " +
                        "accountId=${account.id}, " +
                        "userId=${account.userId}, " +
                        "role=${account.role}"
            )

            return ClassStatsResult
                .AccessDenied
        }

        return try {
            val allStatistics =
                ClassStatsRepository
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

            val pageResults =
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
                ClassStatsPageResponse(
                    count =
                        totalRecords,

                    next =
                        nextPageUrl,

                    previous =
                        previousPageUrl,

                    results =
                        pageResults
                )

            println(
                "Class statistics returned: " +
                        "accountId=${account.id}, " +
                        "page=$safePage, " +
                        "pageSize=$safePageSize, " +
                        "count=${response.count}, " +
                        "results=${response.results}"
            )

            ClassStatsResult.Success(
                statistics =
                    response
            )
        } catch (exception: Exception) {
            println(
                "Class statistics failed: " +
                        "accountId=${account.id}, " +
                        "errorType=${exception::class.simpleName}, " +
                        "message=${exception.message}"
            )

            ClassStatsResult.Failed
        }
    }

    private fun buildPageUrl(
        requestPath: String,
        page: Int,
        pageSize: Int
    ): String {
        return (
                requestPath +
                        "?page=$page" +
                        "&page_size=$pageSize"
                )
    }
}