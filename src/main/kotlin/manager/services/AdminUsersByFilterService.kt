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
                exception.printStackTrace()

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
                            cleanFilter,

                        page =
                            requestedPage,

                        pageSize =
                            safePageSize
                    )

            val totalCount =
                repositoryResult.totalCount

            val totalPages =
                calculateTotalPages(
                    totalCount =
                        totalCount,

                    pageSize =
                        safePageSize
                )

            val safePage =
                requestedPage.coerceIn(
                    minimumValue = 1,
                    maximumValue = totalPages
                )

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
                                repositoryResult.users
                        )
                )

            AdminUsersByFilterResult.Success(
                response =
                    response
            )
        } catch (
            exception: Exception
        ) {
            exception.printStackTrace()

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

}