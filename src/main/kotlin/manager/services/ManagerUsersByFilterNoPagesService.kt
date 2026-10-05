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
            "Manager non-paginated users-by-filter service started"
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
                "Non-paginated users-by-filter request rejected"
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

        val managerRow =
            try {
                ManagerUsersRepository
                    .findManagerAccount(
                        accountId =
                            managerAccountId
                    )
            } catch (exception: Exception) {
                println(
                    "Unable to retrieve authenticated Manager account"
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
                        "Authenticated Manager account was not found"
                    )

                    println(
                        "Manager account ID: $managerAccountId"
                    )

                    println(
                        "=================================================="
                    )

                    return ManagerUsersByFilterNoPagesResult
                        .ManagerAccountNotFound
                }

        println(
            "Authenticated Manager account was found"
        )

        val managerIsActive =
            try {
                ManagerUsersRepository
                    .isActive(
                        managerRow
                    )
            } catch (exception: Exception) {
                println(
                    "Unable to check Manager account active status"
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
            "Manager account active: $managerIsActive"
        )

        if (!managerIsActive) {
            println(
                "Non-paginated users-by-filter request rejected"
            )

            println(
                "Reason: Manager account is inactive"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterNoPagesResult
                .ManagerAccountInactive
        }

        val accountIsManager =
            try {
                ManagerUsersRepository
                    .isManager(
                        managerRow
                    )
            } catch (exception: Exception) {
                println(
                    "Unable to check authenticated account role"
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
            "Authenticated account has Manager role: $accountIsManager"
        )

        if (!accountIsManager) {
            println(
                "Non-paginated users-by-filter request rejected"
            )

            println(
                "Reason: Authenticated account is not a Manager"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterNoPagesResult
                .AccessDenied
        }

        val managerRegionId =
            try {
                ManagerUsersRepository
                    .getManagerRegionId(
                        managerRow
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
                        "Non-paginated users-by-filter request rejected"
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

                    return ManagerUsersByFilterNoPagesResult
                        .ManagerRegionNotAssigned
                }

        println(
            "Manager region ID: $managerRegionId"
        )

        return try {
            println(
                "Retrieving all matching users without pagination"
            )

            val repositoryResult =
                ManagerUsersByFilterRepository
                    .getUsersByFilter(
                        managerRegionId =
                            managerRegionId,

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
                "Manager non-paginated users-by-filter completed successfully"
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
                            "fullName=${account.fullName}"
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
                "Manager non-paginated users-by-filter service failed"
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