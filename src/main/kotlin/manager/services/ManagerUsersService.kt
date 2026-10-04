package com.hr.manager.services

import com.hr.manager.dtos.ManagerUsersPageResponse
import com.hr.manager.repositories.ManagerUsersRepository
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

object ManagerUsersService {

    suspend fun getUsersInManagerRegion(
        accountId: Int,
        page: Int,
        pageSize: Int,
        requestPath: String
    ): ManagerUsersResult {
        println(
            "Manager region-users service started"
        )

        println(
            "Authenticated account ID: $accountId"
        )

        val managerRow =
            ManagerUsersRepository
                .findManagerAccount(
                    accountId
                )
                ?: return ManagerUsersResult
                    .AccountNotFound

        if (
            !ManagerUsersRepository
                .isActive(
                    managerRow
                )
        ) {
            println(
                "Manager region-users request rejected: " +
                        "account is inactive"
            )

            return ManagerUsersResult
                .AccountInactive
        }

        if (
            !ManagerUsersRepository
                .isManager(
                    managerRow
                )
        ) {
            println(
                "Manager region-users request rejected: " +
                        "account does not have Manager role"
            )

            return ManagerUsersResult
                .AccessDenied
        }

        val managerRegionId =
            ManagerUsersRepository
                .getManagerRegionId(
                    managerRow
                )
                ?: return ManagerUsersResult
                    .RegionNotAssigned

        println(
            "Manager region ID: $managerRegionId"
        )

        return try {
            val allUsers =
                ManagerUsersRepository
                    .getActiveUsersInRegion(
                        managerRegionId
                    )

            val safePageSize =
                pageSize.coerceIn(
                    minimumValue = 1,
                    maximumValue = 100
                )

            val totalRecords =
                allUsers.size

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
                ManagerUsersPageResponse(
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
                "Manager region-users response created"
            )

            println(
                "Manager account ID: $accountId"
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
                "Total users: $totalRecords"
            )

            println(
                "Users returned on page: ${pageResults.size}"
            )

            ManagerUsersResult.Success(
                response
            )
        } catch (exception: Exception) {
            println(
                "Manager region-users service failed"
            )

            println(
                "Error type: ${exception::class.simpleName}"
            )

            println(
                "Error message: ${exception.message}"
            )

            ManagerUsersResult.Failed
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