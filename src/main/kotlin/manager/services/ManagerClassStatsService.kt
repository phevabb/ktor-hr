package com.hr.manager.services

import com.hr.manager.dtos.ManagerClassStatsPageResponse
import com.hr.manager.repositories.ManagerClassStatsRepository
import com.hr.manager.repositories.ManagerUsersRepository
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

object ManagerClassStatsService {

    suspend fun getClassStats(
        managerAccountId: Int,
        page: Int,
        pageSize: Int,
        requestPath: String
    ): ManagerClassStatsResult {
        println(
            "=================================================="
        )

        println(
            "Manager class statistics service started"
        )

        println(
            "Authenticated account ID: $managerAccountId"
        )

        val managerRow =
            ManagerUsersRepository
                .findManagerAccount(
                    managerAccountId
                )
                ?: run {
                    println(
                        "Authenticated Manager account was not found"
                    )

                    println(
                        "Account ID: $managerAccountId"
                    )

                    println(
                        "=================================================="
                    )

                    return ManagerClassStatsResult
                        .AccountNotFound
                }

        println(
            "Authenticated account was found"
        )

        val managerIsActive =
            ManagerUsersRepository
                .isActive(
                    managerRow
                )

        println(
            "Manager account active: $managerIsActive"
        )

        if (!managerIsActive) {
            println(
                "Manager class statistics request rejected"
            )

            println(
                "Reason: Manager account is inactive"
            )

            println(
                "=================================================="
            )

            return ManagerClassStatsResult
                .AccountInactive
        }

        val accountIsManager =
            ManagerUsersRepository
                .isManager(
                    managerRow
                )

        println(
            "Account has Manager role: $accountIsManager"
        )

        if (!accountIsManager) {
            println(
                "Manager class statistics request rejected"
            )

            println(
                "Reason: Authenticated account does not have the Manager role"
            )

            println(
                "=================================================="
            )

            return ManagerClassStatsResult
                .AccessDenied
        }

        val managerRegionId =
            ManagerUsersRepository
                .getManagerRegionId(
                    managerRow
                )
                ?: run {
                    println(
                        "Manager class statistics request rejected"
                    )

                    println(
                        "Reason: Manager does not have an assigned region"
                    )

                    println(
                        "Manager account ID: $managerAccountId"
                    )

                    println(
                        "=================================================="
                    )

                    return ManagerClassStatsResult
                        .RegionNotAssigned
                }

        println(
            "Manager region ID: $managerRegionId"
        )

        return try {
            println(
                "Retrieving class statistics for the Manager region"
            )

            val allStatistics =
                ManagerClassStatsRepository
                    .getClassStatsForRegion(
                        regionId =
                            managerRegionId
                    )

            println(
                "Class statistics retrieved successfully"
            )

            println(
                "Total class statistic records: ${allStatistics.size}"
            )

            val safePageSize =
                pageSize.coerceIn(
                    minimumValue = 1,
                    maximumValue = 100
                )

            val totalRecords =
                allStatistics.size

            val totalPages =
                if (totalRecords == 0) {
                    1
                } else {
                    max(
                        1,
                        ceil(
                            totalRecords.toDouble() /
                                    safePageSize.toDouble()
                        ).toInt()
                    )
                }

            val safePage =
                page.coerceIn(
                    minimumValue = 1,
                    maximumValue = totalPages
                )

            println(
                "Requested page: $page"
            )

            println(
                "Validated page: $safePage"
            )

            println(
                "Requested page size: $pageSize"
            )

            println(
                "Validated page size: $safePageSize"
            )

            println(
                "Total records: $totalRecords"
            )

            println(
                "Total pages: $totalPages"
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

            println(
                "Pagination start index: $startIndex"
            )

            println(
                "Pagination end index: $endIndex"
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
                if (
                    safePage > 1
                ) {
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

            println(
                "Next page URL: ${nextPageUrl ?: "None"}"
            )

            println(
                "Previous page URL: ${previousPageUrl ?: "None"}"
            )

            val response =
                ManagerClassStatsPageResponse(
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
                "Manager class statistics response created"
            )

            println(
                "Manager account ID: $managerAccountId"
            )

            println(
                "Manager region ID: $managerRegionId"
            )

            println(
                "Current page: $safePage"
            )

            println(
                "Page size: $safePageSize"
            )

            println(
                "Total class records: ${response.count}"
            )

            println(
                "Results returned: ${response.results.size}"
            )

            response.results.forEach { statistic ->
                println(
                    "Class: ${statistic.className}, " +
                            "count=${statistic.count}"
                )
            }

            println(
                "Manager class statistics service completed successfully"
            )

            println(
                "=================================================="
            )

            ManagerClassStatsResult.Success(
                statistics =
                    response
            )
        } catch (exception: Exception) {
            println(
                "Manager class statistics service failed"
            )

            println(
                "Manager account ID: $managerAccountId"
            )

            println(
                "Manager region ID: $managerRegionId"
            )

            println(
                "Error type: ${exception::class.simpleName}"
            )

            println(
                "Error message: ${exception.message}"
            )

            exception.cause?.let { cause ->
                println(
                    "Cause type: ${cause::class.simpleName}"
                )

                println(
                    "Cause message: ${cause.message}"
                )
            }

            println(
                "=================================================="
            )

            ManagerClassStatsResult.Failed
        }
    }

    private fun buildPageUrl(
        requestPath: String,
        page: Int,
        pageSize: Int
    ): String {
        return "$requestPath" +
                "?page=$page" +
                "&page_size=$pageSize"
    }
}