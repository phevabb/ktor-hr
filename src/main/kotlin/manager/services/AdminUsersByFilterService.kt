package com.hr.manager.services

import com.hr.auth.repositories.AuthRepository
import com.hr.manager.dtos.AdminUsersByFilterResponse
import com.hr.manager.dtos.AdminUsersByFilterResults
import com.hr.manager.repositories.AdminUsersByFilterRepository
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object AdminUsersByFilterService {

    suspend fun getUsersByFilter(
        adminAccountId: Int,
        requestedFilter: String,
        page: Int,
        pageSize: Int,
        requestPath: String
    ): AdminUsersByFilterResult {
        val cleanFilter =
            requestedFilter.trim()

        if (cleanFilter.isBlank()) {
            return AdminUsersByFilterResult
                .FilterRequired
        }

        val account =
            try {
                AuthRepository.findAccountById(
                    adminAccountId
                )
            } catch (
                exception: Exception
            ) {
                printExceptionDetails(
                    exception
                )

                return AdminUsersByFilterResult
                    .Failed
            }
                ?: return AdminUsersByFilterResult
                    .AccountNotFound

        if (!account.isActive) {
            return AdminUsersByFilterResult
                .AccountInactive
        }

        val accountIsAdmin =
            account.role.equals(
                other = "Admin",
                ignoreCase = true
            )

        if (!accountIsAdmin) {
            return AdminUsersByFilterResult
                .AccessDenied
        }

        val safePageSize =
            pageSize.coerceIn(
                minimumValue = 1,
                maximumValue = 100
            )

        val requestedPage =
            page.coerceAtLeast(1)

        return try {
            val repositoryResult =
                AdminUsersByFilterRepository
                    .getUsersByFilter(
                        requestedFilter =
                            cleanFilter
                    )

            val allUsers =
                repositoryResult.users

            val totalCount =
                allUsers.size.toLong()

            val totalPages =
                calculateTotalPages(
                    totalCount = totalCount,
                    pageSize = safePageSize
                )

            val safePage =
                requestedPage.coerceIn(
                    minimumValue = 1,
                    maximumValue = totalPages
                )

            val startIndex =
                (
                        safePage - 1
                        ) * safePageSize

            val safeStartIndex =
                startIndex.coerceAtMost(
                    allUsers.size
                )

            val endIndex =
                (
                        safeStartIndex +
                                safePageSize
                        ).coerceAtMost(
                        allUsers.size
                    )

            val pageUsers =
                if (
                    safeStartIndex <
                    endIndex
                ) {
                    allUsers.subList(
                        fromIndex =
                            safeStartIndex,

                        toIndex =
                            endIndex
                    )
                } else {
                    emptyList()
                }

            val nextPageUrl =
                if (safePage < totalPages) {
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
                if (safePage > 1) {
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
                        totalCount,

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
                                totalCount,

                            users =
                                pageUsers
                        )
                )

            AdminUsersByFilterResult.Success(
                response =
                    response
            )
        } catch (
            exception: Exception
        ) {
            printExceptionDetails(
                exception
            )

            AdminUsersByFilterResult
                .Failed
        }
    }

    private fun calculateTotalPages(
        totalCount: Long,
        pageSize: Int
    ): Int {
        if (totalCount <= 0L) {
            return 1
        }

        return (
                (
                        totalCount +
                                pageSize.toLong() -
                                1L
                        ) /
                        pageSize.toLong()
                ).toInt()
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
                "Cause $causeLevel type: " +
                        "${currentCause::class.simpleName}"
            )

            println(
                "Cause $causeLevel message: " +
                        "${currentCause.message}"
            )

            currentCause =
                currentCause.cause

            causeLevel +=
                1
        }
    }
}