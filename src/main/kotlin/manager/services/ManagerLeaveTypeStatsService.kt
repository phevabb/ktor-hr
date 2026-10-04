package com.hr.manager.services

import com.hr.manager.dtos.ManagerLeaveTypeStatsPageResponse
import com.hr.manager.repositories.ManagerLeaveTypeStatsRepository
import com.hr.manager.repositories.ManagerUsersRepository
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

object ManagerLeaveTypeStatsService {

    suspend fun getLeaveTypeStats(
        managerAccountId: Int,
        page: Int,
        pageSize: Int,
        requestPath: String
    ): ManagerLeaveTypeStatsResult {
        println(
            "=================================================="
        )

        println(
            "Manager leave-type statistics service started"
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

                    return ManagerLeaveTypeStatsResult
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
                "Manager leave-type statistics request rejected"
            )

            println(
                "Reason: Manager account is inactive"
            )

            println(
                "=================================================="
            )

            return ManagerLeaveTypeStatsResult
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
                "Manager leave-type statistics request rejected"
            )

            println(
                "Reason: Authenticated account does not have the Manager role"
            )

            println(
                "=================================================="
            )

            return ManagerLeaveTypeStatsResult
                .AccessDenied
        }

        val managerRegionId =
            ManagerUsersRepository
                .getManagerRegionId(
                    managerRow
                )
                ?: run {
                    println(
                        "Manager leave-type statistics request rejected"
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

                    return ManagerLeaveTypeStatsResult
                        .RegionNotAssigned
                }

        println(
            "Manager region ID: $managerRegionId"
        )

        return try {
            println(
                "Retrieving leave-type statistics for the Manager region"
            )

            val allStatistics =
                ManagerLeaveTypeStatsRepository
                    .getLeaveTypeStatsForRegion(
                        regionId =
                            managerRegionId
                    )

            println(
                "Leave-type statistics retrieved successfully"
            )

            println(
                "Total leave-type statistic records: ${allStatistics.size}"
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

            println(
                "Leave-type statistics on current page: ${pageResults.size}"
            )

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
                ManagerLeaveTypeStatsPageResponse(
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
                "Manager leave-type statistics response created"
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
                "Total leave-type records: ${response.count}"
            )

            println(
                "Results returned: ${response.results.size}"
            )

            response.results.forEach { statistic ->
                println(
                    "Leave type: ${statistic.leaveType}, " +
                            "count=${statistic.count}"
                )
            }

            println(
                "Manager leave-type statistics service completed successfully"
            )

            println(
                "=================================================="
            )

            ManagerLeaveTypeStatsResult.Success(
                statistics =
                    response
            )
        } catch (exception: Exception) {
            println(
                "Manager leave-type statistics service failed"
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

            ManagerLeaveTypeStatsResult.Failed
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