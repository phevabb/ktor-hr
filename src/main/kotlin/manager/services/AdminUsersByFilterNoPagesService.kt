package com.hr.superadmin.services

import com.hr.auth.repositories.AuthRepository
import com.hr.superadmin.dtos.AdminUsersByFilterNoPagesResponse
import com.hr.superadmin.repositories.AdminUsersByFilterRepository

object AdminUsersByFilterNoPagesService {

    suspend fun getUsersByFilter(
        adminAccountId: Int,
        requestedFilter: String
    ): AdminUsersByFilterNoPagesResult {
        println(
            "=================================================="
        )

        println(
            "Admin non-paginated users-by-filter service started"
        )

        println(
            "Authenticated account ID: $adminAccountId"
        )

        println(
            "Requested filter: $requestedFilter"
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

            return AdminUsersByFilterNoPagesResult
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

                return AdminUsersByFilterNoPagesResult
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

                    return AdminUsersByFilterNoPagesResult
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

            return AdminUsersByFilterNoPagesResult
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

            return AdminUsersByFilterNoPagesResult
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

            val response =
                AdminUsersByFilterNoPagesResponse(
                    dept =
                        cleanFilter,

                    filterType =
                        repositoryResult.filterType,

                    count =
                        repositoryResult.users.size,

                    users =
                        repositoryResult.users
                )

            println(
                "Admin non-paginated users-by-filter completed successfully"
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
                "Total matching users: ${response.count}"
            )

            println(
                "=================================================="
            )

            AdminUsersByFilterNoPagesResult.Success(
                response =
                    response
            )
        } catch (exception: Exception) {
            println(
                "Admin non-paginated users-by-filter failed"
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

            AdminUsersByFilterNoPagesResult
                .Failed
        }
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