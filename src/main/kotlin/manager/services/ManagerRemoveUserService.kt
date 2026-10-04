package com.hr.manager.services

import com.hr.manager.dtos.ManagerRemoveUserRequest
import com.hr.manager.dtos.ManagerRemoveUserResponse
import com.hr.manager.repositories.ManagerRemoveUserRepository
import com.hr.manager.repositories.ManagerRemoveUserRepositoryResult
import com.hr.manager.repositories.ManagerUsersRepository

object ManagerRemoveUserService {

    suspend fun removeUser(
        managerAccountId: Int,
        request: ManagerRemoveUserRequest
    ): ManagerRemoveUserResult {
        println(
            "=================================================="
        )

        println(
            "Manager remove-user service started"
        )

        println(
            "Authenticated Manager account ID: $managerAccountId"
        )

        println(
            "Target account ID: ${request.accountId}"
        )

        val reason =
            request.reason.trim()

        if (request.accountId <= 0) {
            println(
                "Remove-user request rejected"
            )

            println(
                "Reason: Target account ID is invalid"
            )

            println(
                "=================================================="
            )

            return ManagerRemoveUserResult
                .InvalidAccountId
        }

        if (reason.isBlank()) {
            println(
                "Remove-user request rejected"
            )

            println(
                "Reason: Removal reason is blank"
            )

            println(
                "=================================================="
            )

            return ManagerRemoveUserResult
                .ReasonRequired
        }

        if (reason.length > 255) {
            println(
                "Remove-user request rejected"
            )

            println(
                "Reason: Removal reason exceeds 255 characters"
            )

            println(
                "Submitted reason length: ${reason.length}"
            )

            println(
                "=================================================="
            )

            return ManagerRemoveUserResult
                .ReasonTooLong
        }

        if (
            request.accountId ==
            managerAccountId
        ) {
            println(
                "Remove-user request rejected"
            )

            println(
                "Reason: Manager cannot remove the Manager's own account"
            )

            println(
                "=================================================="
            )

            return ManagerRemoveUserResult
                .CannotRemoveSelf
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

                printRemoveUserException(
                    exception
                )

                println(
                    "=================================================="
                )

                return ManagerRemoveUserResult
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

                    return ManagerRemoveUserResult
                        .ManagerAccountNotFound
                }

        val managerIsActive =
            try {
                ManagerUsersRepository
                    .isActive(
                        managerRow
                    )
            } catch (exception: Exception) {
                println(
                    "Unable to check Manager active status"
                )

                printRemoveUserException(
                    exception
                )

                println(
                    "=================================================="
                )

                return ManagerRemoveUserResult
                    .Failed
            }

        println(
            "Manager account active: $managerIsActive"
        )

        if (!managerIsActive) {
            println(
                "Remove-user request rejected"
            )

            println(
                "Reason: Manager account is inactive"
            )

            println(
                "=================================================="
            )

            return ManagerRemoveUserResult
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

                printRemoveUserException(
                    exception
                )

                println(
                    "=================================================="
                )

                return ManagerRemoveUserResult
                    .Failed
            }

        println(
            "Authenticated account has Manager role: $accountIsManager"
        )

        if (!accountIsManager) {
            println(
                "Remove-user request rejected"
            )

            println(
                "Reason: Authenticated account is not a Manager"
            )

            println(
                "=================================================="
            )

            return ManagerRemoveUserResult
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

                printRemoveUserException(
                    exception
                )

                println(
                    "=================================================="
                )

                return ManagerRemoveUserResult
                    .Failed
            }
                ?: run {
                    println(
                        "Remove-user request rejected"
                    )

                    println(
                        "Reason: Manager does not have an assigned region"
                    )

                    println(
                        "=================================================="
                    )

                    return ManagerRemoveUserResult
                        .ManagerRegionNotAssigned
                }

        println(
            "Manager region ID: $managerRegionId"
        )

        return try {
            val repositoryResult =
                ManagerRemoveUserRepository
                    .removeUser(
                        accountId =
                            request.accountId,

                        managerRegionId =
                            managerRegionId,

                        reason =
                            reason
                    )

            when (repositoryResult) {
                is ManagerRemoveUserRepositoryResult.Success -> {
                    val response =
                        ManagerRemoveUserResponse(
                            message =
                                "User removed successfully",

                            accountId =
                                repositoryResult.accountId,

                            userId =
                                repositoryResult.userId,

                            removedAt =
                                repositoryResult
                                    .removedAt
                                    .toString()
                        )

                    println(
                        "Manager remove-user service completed successfully"
                    )

                    println(
                        "Removed account ID: ${response.accountId}"
                    )

                    println(
                        "Removed user ID: ${response.userId}"
                    )

                    println(
                        "Removal timestamp: ${response.removedAt}"
                    )

                    println(
                        "=================================================="
                    )

                    ManagerRemoveUserResult.Success(
                        response =
                            response
                    )
                }

                ManagerRemoveUserRepositoryResult.UserNotFound -> {
                    println(
                        "Target account was not found"
                    )

                    println(
                        "=================================================="
                    )

                    ManagerRemoveUserResult
                        .UserNotFound
                }

                ManagerRemoveUserRepositoryResult.UserAlreadyInactive -> {
                    println(
                        "Target account is already inactive"
                    )

                    println(
                        "=================================================="
                    )

                    ManagerRemoveUserResult
                        .UserAlreadyInactive
                }

                ManagerRemoveUserRepositoryResult.UserRegionNotAssigned -> {
                    println(
                        "Target account does not have an assigned region"
                    )

                    println(
                        "=================================================="
                    )

                    ManagerRemoveUserResult
                        .UserRegionNotAssigned
                }

                ManagerRemoveUserRepositoryResult.UserOutsideManagerRegion -> {
                    println(
                        "Target account is outside the Manager region"
                    )

                    println(
                        "=================================================="
                    )

                    ManagerRemoveUserResult
                        .UserOutsideManagerRegion
                }

                ManagerRemoveUserRepositoryResult.Failed -> {
                    println(
                        "Account removal repository operation failed"
                    )

                    println(
                        "=================================================="
                    )

                    ManagerRemoveUserResult
                        .Failed
                }
            }
        } catch (exception: Exception) {
            println(
                "Manager remove-user service failed"
            )

            println(
                "Manager account ID: $managerAccountId"
            )

            println(
                "Manager region ID: $managerRegionId"
            )

            println(
                "Target account ID: ${request.accountId}"
            )

            printRemoveUserException(
                exception
            )

            println(
                "=================================================="
            )

            ManagerRemoveUserResult
                .Failed
        }
    }

    private fun printRemoveUserException(
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