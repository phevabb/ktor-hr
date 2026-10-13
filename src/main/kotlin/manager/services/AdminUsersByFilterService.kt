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
        val serviceStartedAt =
            System.currentTimeMillis()

        println(
            "--------------------------------------------------"
        )

        println(
            "AdminUsersByFilterService started"
        )

        val filterPreparationStartedAt =
            System.currentTimeMillis()

        val cleanFilter =
            requestedFilter.trim()

        val safePageSize =
            pageSize.coerceIn(
                minimumValue = 1,
                maximumValue = 100
            )

        val requestedPage =
            page.coerceAtLeast(1)

        println(
            "Service parameter preparation duration: ${
                System.currentTimeMillis() -
                        filterPreparationStartedAt
            } ms"
        )

        if (cleanFilter.isBlank()) {
            println(
                "Service rejected request because filter is blank"
            )

            println(
                "Service total duration: ${
                    System.currentTimeMillis() -
                            serviceStartedAt
                } ms"
            )

            println(
                "--------------------------------------------------"
            )

            return AdminUsersByFilterResult
                .FilterRequired
        }

        val accountLookupStartedAt =
            System.currentTimeMillis()

        val account =
            try {
                AuthRepository.findAccountById(
                    adminAccountId
                )
            } catch (
                exception: Exception
            ) {
                println(
                    "Admin account lookup failed"
                )

                println(
                    "Admin account lookup duration: ${
                        System.currentTimeMillis() -
                                accountLookupStartedAt
                    } ms"
                )

                exception.printStackTrace()

                println(
                    "Service total failed duration: ${
                        System.currentTimeMillis() -
                                serviceStartedAt
                    } ms"
                )

                println(
                    "--------------------------------------------------"
                )

                return AdminUsersByFilterResult
                    .Failed
            }

        println(
            "Admin account lookup duration: ${
                System.currentTimeMillis() -
                        accountLookupStartedAt
            } ms"
        )

        if (account == null) {
            println(
                "Authenticated Admin account was not found"
            )

            println(
                "Service total duration: ${
                    System.currentTimeMillis() -
                            serviceStartedAt
                } ms"
            )

            println(
                "--------------------------------------------------"
            )

            return AdminUsersByFilterResult
                .AccountNotFound
        }

        val authorizationStartedAt =
            System.currentTimeMillis()

        if (!account.isActive) {
            println(
                "Authorization check duration: ${
                    System.currentTimeMillis() -
                            authorizationStartedAt
                } ms"
            )

            println(
                "Service rejected inactive account"
            )

            println(
                "Service total duration: ${
                    System.currentTimeMillis() -
                            serviceStartedAt
                } ms"
            )

            println(
                "--------------------------------------------------"
            )

            return AdminUsersByFilterResult
                .AccountInactive
        }

        val accountIsAdmin =
            account.role.equals(
                other = "Admin",
                ignoreCase = true
            )

        if (!accountIsAdmin) {
            println(
                "Authorization check duration: ${
                    System.currentTimeMillis() -
                            authorizationStartedAt
                } ms"
            )

            println(
                "Service rejected non-Admin account"
            )

            println(
                "Service total duration: ${
                    System.currentTimeMillis() -
                            serviceStartedAt
                } ms"
            )

            println(
                "--------------------------------------------------"
            )

            return AdminUsersByFilterResult
                .AccessDenied
        }

        println(
            "Authorization check duration: ${
                System.currentTimeMillis() -
                        authorizationStartedAt
            } ms"
        )

        return try {
            val repositoryStartedAt =
                System.currentTimeMillis()

            println(
                "Repository execution started"
            )

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

            val repositoryDuration =
                System.currentTimeMillis() -
                        repositoryStartedAt

            println(
                "Repository execution duration: $repositoryDuration ms"
            )

            println(
                "Repository total matching users: ${
                    repositoryResult.totalCount
                }"
            )

            println(
                "Repository users returned: ${
                    repositoryResult.users.size
                }"
            )

            println(
                "Repository filter type: ${
                    repositoryResult.filterType
                        ?: "None"
                }"
            )

            val paginationStartedAt =
                System.currentTimeMillis()

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

            println(
                "Pagination calculation duration: ${
                    System.currentTimeMillis() -
                            paginationStartedAt
                } ms"
            )

            val responseStartedAt =
                System.currentTimeMillis()

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

            println(
                "Service response construction duration: ${
                    System.currentTimeMillis() -
                            responseStartedAt
                } ms"
            )

            println(
                "Service total duration: ${
                    System.currentTimeMillis() -
                            serviceStartedAt
                } ms"
            )

            println(
                "--------------------------------------------------"
            )

            AdminUsersByFilterResult.Success(
                response =
                    response
            )
        } catch (
            exception: Exception
        ) {
            println(
                "AdminUsersByFilterService failed"
            )

            println(
                "Error type: ${exception::class.simpleName}"
            )

            println(
                "Error message: ${exception.message}"
            )

            exception.printStackTrace()

            println(
                "Service total failed duration: ${
                    System.currentTimeMillis() -
                            serviceStartedAt
                } ms"
            )

            println(
                "--------------------------------------------------"
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
}