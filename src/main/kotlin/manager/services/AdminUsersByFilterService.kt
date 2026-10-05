package com.hr.superadmin.services

import com.hr.auth.repositories.AuthRepository
import com.hr.superadmin.dtos.AdminUsersByFilterResponse
import com.hr.superadmin.dtos.AdminUsersByFilterResults
import com.hr.superadmin.repositories.AdminUsersByFilterRepository
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

object AdminUsersByFilterService {

    suspend fun getUsersByFilter(
        adminAccountId: Int,
        requestedFilter: String,
        page: Int,
        pageSize: Int,
        requestPath: String
    ): AdminUsersByFilterResult {
        println(
            "=================================================="
        )

        println(
            "Admin paginated users-by-filter service started"
        )

        println(
            "Authenticated account ID: $adminAccountId"
        )

        println(
            "Requested filter: $requestedFilter"
        )

        println(
            "Requested page: $page"
        )

        println(
            "Requested page size: $pageSize"
        )

        val cleanFilter =
            requestedFilter.trim()

        if (cleanFilter.isBlank()) {
            println(
                "Admin users-by-filter request rejected"
            )

            println(
                "Reason: dept filter is blank"
            )

            println(
                "=================================================="
            )

            return AdminUsersByFilterResult
                .FilterRequired
        }

        val account =
            try {
                AuthRepository
                    .findAccountById(
                        adminAccountId
                    )
            } catch (exception: Exception) {
                println(
                    "Unable to retrieve authenticated Admin account"
                )

                printExceptionDetails(
                    exception
                )

                println(
                    "=================================================="
                )

                return AdminUsersByFilterResult
                    .Failed
            }
                ?: run {
                    println(
                        "Authenticated Admin account was not found"
                    )

                    println(
                        "Account ID: $adminAccountId"
                    )

                    println(
                        "=================================================="
                    )

                    return AdminUsersByFilterResult
                        .AccountNotFound
                }

        println(
            "Authenticated account was found"
        )

        println(
            "Authenticated account ID: ${account.id}"
        )

        println(
            "Authenticated user ID: ${account.userId}"
        )

        println(
            "Authenticated account role: ${account.role}"
        )

        println(
            "Authenticated account active: ${account.isActive}"
        )

        if (!account.isActive) {
            println(
                "Admin users-by-filter request rejected"
            )

            println(
                "Reason: Authenticated account is inactive"
            )

            println(
                "=================================================="
            )

            return AdminUsersByFilterResult
                .AccountInactive
        }

        val accountIsAdmin =
            account.role.equals(
                other =
                    "Admin",

                ignoreCase =
                    true
            )

        println(
            "Authenticated account has Admin role: $accountIsAdmin"
        )

        if (!accountIsAdmin) {
            println(
                "Admin users-by-filter request rejected"
            )

            println(
                "Reason: Authenticated account is not an Admin"
            )

            println(
                "Current role: ${account.role}"
            )

            println(
                "=================================================="
            )

            return AdminUsersByFilterResult
                .AccessDenied
        }

        return try {
            println(
                "Retrieving matching active users across all regions"
            )

            val repositoryResult =
                AdminUsersByFilterRepository
                    .getUsersByFilter(
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
                AdminUsersByFilterResponse(
                    count =
                        totalRecords,

                    next =
                        nextPageUrl,

                    previous =
                        previousPageUrl,

                    results =
                        AdminUsersByFilterResults(
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
                "Admin paginated users-by-filter completed successfully"
            )

            println(
                "Admin account ID: $adminAccountId"
            )

            println(
                "Requested filter: $cleanFilter"
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
                "Page size: $safePageSize"
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

            AdminUsersByFilterResult.Success(
                response =
                    response
            )
        } catch (exception: Exception) {
            println(
                "Admin paginated users-by-filter failed"
            )

            println(
                "Admin account ID: $adminAccountId"
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

            AdminUsersByFilterResult
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