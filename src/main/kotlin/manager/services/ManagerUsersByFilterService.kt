package com.hr.manager.services

import com.hr.manager.dtos.ManagerUsersByFilterResponse
import com.hr.manager.dtos.ManagerUsersByFilterResults
import com.hr.manager.repositories.ManagerUsersByFilterRepository
import com.hr.manager.repositories.ManagerUsersByFilterResult
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
            "Users-by-filter service started"
        )

        println(
            "Authenticated account ID: $managerAccountId"
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

        val accountRow =
            try {
                ManagerUsersRepository
                    .findManagerAccount(
                        accountId =
                            managerAccountId
                    )
            } catch (exception: Exception) {
                println(
                    "Unable to retrieve authenticated account"
                )

                printExceptionDetails(
                    exception
                )

                println(
                    "=================================================="
                )

                return ManagerUsersByFilterResult
                    .Failed
            }
                ?: run {
                    println(
                        "Authenticated account was not found"
                    )

                    println(
                        "Account ID: $managerAccountId"
                    )

                    println(
                        "=================================================="
                    )

                    return ManagerUsersByFilterResult
                        .ManagerAccountNotFound
                }

        println(
            "Authenticated account was found"
        )

        val accountIsActive =
            try {
                ManagerUsersRepository
                    .isActive(
                        accountRow
                    )
            } catch (exception: Exception) {
                println(
                    "Unable to check authenticated account active status"
                )

                printExceptionDetails(
                    exception
                )

                println(
                    "=================================================="
                )

                return ManagerUsersByFilterResult
                    .Failed
            }

        println(
            "Authenticated account active status: $accountIsActive"
        )

        if (!accountIsActive) {
            println(
                "Users-by-filter request rejected"
            )

            println(
                "Reason: Authenticated account is inactive"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterResult
                .ManagerAccountInactive
        }

        val accountRole =
            try {
                ManagerUsersRepository
                    .getAccountRole(
                        accountRow
                    )
            } catch (exception: Exception) {
                println(
                    "Unable to retrieve authenticated account role"
                )

                printExceptionDetails(
                    exception
                )

                println(
                    "=================================================="
                )

                return ManagerUsersByFilterResult
                    .Failed
            }

        println(
            "Authenticated account database role: $accountRole"
        )

        val accountIsAdmin =
            accountRole.equals(
                other =
                    "Admin",

                ignoreCase =
                    true
            )

        val accountIsManager =
            accountRole.equals(
                other =
                    "Manager",

                ignoreCase =
                    true
            )

        println(
            "Authenticated account is Admin: $accountIsAdmin"
        )

        println(
            "Authenticated account is Manager: $accountIsManager"
        )

        if (
            !accountIsAdmin &&
            !accountIsManager
        ) {
            println(
                "Users-by-filter request rejected"
            )

            println(
                "Reason: Admin or Manager access is required"
            )

            println(
                "Current role: $accountRole"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterResult
                .AccessDenied
        }

        /*
         * A null region scope means access to all regions.
         *
         * Admin:
         * regionScopeId = null
         *
         * Manager:
         * regionScopeId = assigned region ID
         */
        val regionScopeId =
            if (accountIsAdmin) {
                println(
                    "Admin access granted across all regions"
                )

                null
            } else {
                try {
                    ManagerUsersRepository
                        .getManagerRegionId(
                            accountRow
                        )
                } catch (exception: Exception) {
                    println(
                        "Unable to retrieve Manager region"
                    )

                    printExceptionDetails(
                        exception
                    )

                    println(
                        "=================================================="
                    )

                    return ManagerUsersByFilterResult
                        .Failed
                }
                    ?: run {
                        println(
                            "Users-by-filter request rejected"
                        )

                        println(
                            "Reason: Manager has no assigned region"
                        )

                        println(
                            "Manager account ID: $managerAccountId"
                        )

                        println(
                            "=================================================="
                        )

                        return ManagerUsersByFilterResult
                            .ManagerRegionNotAssigned
                    }
            }

        println(
            "Applied region scope: ${regionScopeId ?: "ALL REGIONS"}"
        )

        return try {
            val repositoryResult =
                ManagerUsersByFilterRepository
                    .getUsersByFilter(
                        regionScopeId =
                            regionScopeId,

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
                                repositoryResult.filterType,

                            count =
                                totalRecords,

                            users =
                                pageUsers
                        )
                )

            println(
                "Users-by-filter completed successfully"
            )

            println(
                "Authenticated account ID: $managerAccountId"
            )

            println(
                "Authenticated account role: $accountRole"
            )

            println(
                "Applied region scope: ${regionScopeId ?: "ALL REGIONS"}"
            )

            println(
                "Filter: $cleanFilter"
            )

            println(
                "Filter type: ${repositoryResult.filterType ?: "None"}"
            )

            println(
                "Total matching users: $totalRecords"
            )

            println(
                "Current page: $safePage"
            )

            println(
                "Validated page size: $safePageSize"
            )

            println(
                "Total pages: $totalPages"
            )

            println(
                "Users returned: ${pageUsers.size}"
            )

            println(
                "Next page URL: ${nextPageUrl ?: "None"}"
            )

            println(
                "Previous page URL: ${previousPageUrl ?: "None"}"
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
                "Users-by-filter service failed"
            )

            println(
                "Authenticated account ID: $managerAccountId"
            )

            println(
                "Authenticated account role: $accountRole"
            )

            println(
                "Applied region scope: ${regionScopeId ?: "ALL REGIONS"}"
            )

            println(
                "Requested filter: $cleanFilter"
            )

            printExceptionDetails(
                exception
            )

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

    private fun printExceptionDetails(
        exception: Exception
    ) {
        println(
            "Error type: ${exception::class.simpleName}"
        )

        println(
            "Error message: ${exception.message}"
        )

        var currentCause =
            exception.cause

        var causeLevel =
            1

        while (currentCause != null) {
            println(
                "Cause $causeLevel type: ${currentCause::class.simpleName}"
            )

            println(
                "Cause $causeLevel message: ${currentCause.message}"
            )

            currentCause =
                currentCause.cause

            causeLevel +=
                1
        }
    }
}