package com.hr.manager.services

import com.hr.manager.dtos.ManagerUsersByFilterNoPagesResponse
import com.hr.manager.repositories.ManagerUsersByFilterRepository
import com.hr.manager.repositories.ManagerUsersRepository

object ManagerUsersByFilterNoPagesService {

    suspend fun getUsersByFilter(
        managerAccountId: Int,
        requestedFilter: String
    ): ManagerUsersByFilterNoPagesResult {
        println(
            "=================================================="
        )

        println(
            "Non-paginated users-by-filter service started"
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

            return ManagerUsersByFilterNoPagesResult
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

                return ManagerUsersByFilterNoPagesResult
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

                    return ManagerUsersByFilterNoPagesResult
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

                return ManagerUsersByFilterNoPagesResult
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

            return ManagerUsersByFilterNoPagesResult
                .ManagerAccountInactive
        }

        val accountRole =
            ManagerUsersRepository
                .getAccountRole(
                    accountRow
                )

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

            return ManagerUsersByFilterNoPagesResult
                .AccessDenied
        }

        /*
         * Admin receives a null region scope, which means
         * users from all regions may be searched.
         *
         * Manager receives the Manager's assigned region ID.
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

                    return ManagerUsersByFilterNoPagesResult
                        .Failed
                }
                    ?: run {
                        println(
                            "Users-by-filter request rejected"
                        )

                        println(
                            "Reason: Manager does not have an assigned region"
                        )

                        println(
                            "Account ID: $managerAccountId"
                        )

                        println(
                            "=================================================="
                        )

                        return ManagerUsersByFilterNoPagesResult
                            .ManagerRegionNotAssigned
                    }
            }

        println(
            "Applied region scope: ${regionScopeId ?: "ALL REGIONS"}"
        )

        return try {
            println(
                "Retrieving all matching users without pagination"
            )

            val repositoryResult =
                ManagerUsersByFilterRepository
                    .getUsersByFilter(
                        regionScopeId =
                            regionScopeId,

                        requestedFilter =
                            cleanFilter
                    )

            val matchedUsers =
                repositoryResult.users

            val response =
                ManagerUsersByFilterNoPagesResponse(
                    dept =
                        cleanFilter,

                    filterType =
                        repositoryResult.filterType,

                    count =
                        matchedUsers.size,

                    users =
                        matchedUsers
                )

            println(
                "Non-paginated users-by-filter completed successfully"
            )

            println(
                "Authenticated account ID: $managerAccountId"
            )

            println(
                "Authenticated role: $accountRole"
            )

            println(
                "Applied region scope: ${regionScopeId ?: "ALL REGIONS"}"
            )

            println(
                "Requested filter: $cleanFilter"
            )

            println(
                "Detected filter type: ${repositoryResult.filterType ?: "None"}"
            )

            println(
                "Total matching users: ${matchedUsers.size}"
            )

            matchedUsers.forEachIndexed {
                    index,
                    account ->

                println(
                    "Matched user ${index + 1}: " +
                            "accountId=${account.id}, " +
                            "userId=${account.userId}, " +
                            "fullName=${account.fullName}, " +
                            "region=${account.regionName}"
                )
            }

            println(
                "=================================================="
            )

            ManagerUsersByFilterNoPagesResult.Success(
                response =
                    response
            )
        } catch (exception: Exception) {
            println(
                "Non-paginated users-by-filter service failed"
            )

            println(
                "Authenticated account ID: $managerAccountId"
            )

            println(
                "Authenticated role: $accountRole"
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

            ManagerUsersByFilterNoPagesResult
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