package com.hr.manager.services

import com.hr.manager.dtos.ManagerUsersByFilterResponse
import com.hr.manager.dtos.ManagerUsersByFilterResults
import com.hr.manager.repositories.ManagerUsersByFilterRepository
import com.hr.manager.repositories.ManagerUsersRepository
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

object ManagerUsersByFilterService {

    suspend fun getUsersByFilter(
        managerAccountId: Int,
        requestedFilter: String,
        page: Int,
        pageSize: Int,
        requestPath: String
    ): ManagerUsersByFilterResult {
        println(
            "=================================================="
        )

        println(
            "Manager users-by-filter service started"
        )

        println(
            "Authenticated Manager account ID: $managerAccountId"
        )

        println(
            "Requested filter: $requestedFilter"
        )

        val cleanFilter =
            requestedFilter.trim()

        if (cleanFilter.isBlank()) {
            println(
                "Users-by-filter request rejected"
            )

            println(
                "Reason: dept filter is blank"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterResult
                .FilterRequired
        }

        val managerRow =
            ManagerUsersRepository
                .findManagerAccount(
                    accountId =
                        managerAccountId
                )
                ?: run {
                    println(
                        "Authenticated Manager account was not found"
                    )

                    println(
                        "=================================================="
                    )

                    return ManagerUsersByFilterResult
                        .ManagerAccountNotFound
                }

        val managerIsActive =
            ManagerUsersRepository
                .isActive(
                    managerRow
                )

        if (!managerIsActive) {
            println(
                "Users-by-filter request rejected"
            )

            println(
                "Reason: Manager account is inactive"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterResult
                .ManagerAccountInactive
        }

        val accountIsManager =
            ManagerUsersRepository
                .isManager(
                    managerRow
                )

        if (!accountIsManager) {
            println(
                "Users-by-filter request rejected"
            )

            println(
                "Reason: Authenticated account is not a Manager"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterResult
                .AccessDenied
        }

        val managerRegionId =
            ManagerUsersRepository
                .getManagerRegionId(
                    managerRow
                )
                ?: run {
                    println(
                        "Users-by-filter request rejected"
                    )

                    println(
                        "Reason: Manager has no assigned region"
                    )

                    println(
                        "=================================================="
                    )

                    return ManagerUsersByFilterResult
                        .ManagerRegionNotAssigned
                }

        println(
            "Manager region ID: $managerRegionId"
        )

        return try {
            val repositoryResult =
                ManagerUsersByFilterRepository
                    .getUsersByFilter(
                        managerRegionId =
                            managerRegionId,

                        requestedFilter =
                            cleanFilter
                    )

            val allUsers =
                repositoryResult.users

            val safePageSize =
                pageSize.coerceIn(
                    minimumValue =
                        1,

                    maximumValue =
                        100
                )

            val totalRecords =
                allUsers.size

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
                    minimumValue =
                        1,

                    maximumValue =
                        totalPages
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

            val pageUsers =
                if (
                    startIndex <
                    endIndex
                ) {
                    allUsers.subList(
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

                        requestedFilter =
                            cleanFilter,

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

                        requestedFilter =
                            cleanFilter,

                        page =
                            safePage - 1,

                        pageSize =
                            safePageSize
                    )
                } else {
                    null
                }

            val response =
                ManagerUsersByFilterResponse(
                    count =
                        totalRecords,

                    next =
                        nextPageUrl,

                    previous =
                        previousPageUrl,

                    results =
                        ManagerUsersByFilterResults(
                            dept =
                                cleanFilter,

                            filterType =
                                repositoryResult
                                    .filterType,

                            count =
                                totalRecords,

                            users =
                                pageUsers
                        )
                )

            println(
                "Manager users-by-filter completed successfully"
            )

            println(
                "Filter: $cleanFilter"
            )

            println(
                "Filter type: ${repositoryResult.filterType}"
            )

            println(
                "Total matching users: $totalRecords"
            )

            println(
                "Current page: $safePage"
            )

            println(
                "Users returned: ${pageUsers.size}"
            )

            println(
                "=================================================="
            )

            ManagerUsersByFilterResult.Success(
                response =
                    response
            )
        } catch (exception: Exception) {
            println(
                "Manager users-by-filter service failed"
            )

            println(
                "Manager account ID: $managerAccountId"
            )

            println(
                "Manager region ID: $managerRegionId"
            )

            println(
                "Requested filter: $cleanFilter"
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

            ManagerUsersByFilterResult
                .Failed
        }
    }

    private fun buildPageUrl(
        requestPath: String,
        requestedFilter: String,
        page: Int,
        pageSize: Int
    ): String {
        val encodedFilter =
            URLEncoder.encode(
                requestedFilter,
                StandardCharsets.UTF_8
                    .toString()
            )

        return "$requestPath" +
                "?dept=$encodedFilter" +
                "&page=$page" +
                "&page_size=$pageSize"
    }
}