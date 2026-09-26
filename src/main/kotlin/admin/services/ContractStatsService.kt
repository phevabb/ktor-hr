package com.hr.admin.services

import com.hr.admin.dtos.ContractStatsPageResponse
import com.hr.admin.repositories.ContractStatsRepository
import com.hr.auth.repositories.AuthRepository
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

object ContractStatsService {

    suspend fun getContractStats(
        accountId: Int,
        page: Int,
        pageSize: Int,
        requestPath: String
    ): ContractStatsResult {
        val account =
            AuthRepository
                .findAccountById(
                    accountId
                )
                ?: return ContractStatsResult
                    .AccountNotFound

        if (!account.isActive) {
            return ContractStatsResult
                .AccountInactive
        }

        val isAdmin =
            account.role.equals(
                other = "Admin",
                ignoreCase = true
            )

        if (!isAdmin) {
            println(
                "Contract statistics access denied: " +
                        "accountId=${account.id}, " +
                        "userId=${account.userId}, " +
                        "role=${account.role}"
            )

            return ContractStatsResult
                .AccessDenied
        }

        return try {
            val allStatistics =
                ContractStatsRepository
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
                        safePage - 1
                        ) * safePageSize

            val safeStartIndex =
                startIndex.coerceAtMost(
                    totalRecords
                )

            val endIndex =
                min(
                    safeStartIndex +
                            safePageSize,
                    totalRecords
                )

            val results =
                if (
                    safeStartIndex <
                    endIndex
                ) {
                    allStatistics.subList(
                        safeStartIndex,
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
                ContractStatsPageResponse(
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
                "Contract statistics returned: " +
                        "accountId=${account.id}, " +
                        "page=$safePage, " +
                        "pageSize=$safePageSize, " +
                        "count=${response.count}, " +
                        "results=${response.results}"
            )

            ContractStatsResult.Success(
                statistics =
                    response
            )
        } catch (exception: Exception) {
            println(
                "Contract statistics failed: " +
                        "accountId=${account.id}, " +
                        "errorType=${exception::class.simpleName}, " +
                        "message=${exception.message}"
            )

            ContractStatsResult.Failed
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