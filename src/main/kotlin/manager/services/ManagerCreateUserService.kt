package com.hr.manager.services

import com.hr.account.dtos.AccountCreateRequest
import com.hr.account.dtos.Role
import com.hr.manager.repositories.ManagerCreateUserRepository
import com.hr.manager.repositories.ManagerUsersRepository

object ManagerCreateUserService {

    suspend fun createUser(
        managerAccountId: Int,
        request: AccountCreateRequest
    ): ManagerCreateUserResult {
        println(
            "=================================================="
        )

        println(
            "Manager account creation service started"
        )

        println(
            "Authenticated Manager account ID: $managerAccountId"
        )

        val managerRow =
            try {
                ManagerCreateUserRepository
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

                return ManagerCreateUserResult
                    .Failed
            }
                ?: run {
                    println(
                        "Authenticated Manager account was not found"
                    )

                    println(
                        "Account ID: $managerAccountId"
                    )

                    println(
                        "=================================================="
                    )

                    return ManagerCreateUserResult
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
                    "Unable to check Manager active status"
                )

                printExceptionDetails(
                    exception
                )

                println(
                    "=================================================="
                )

                return ManagerCreateUserResult
                    .Failed
            }

        println(
            "Manager account active: $managerIsActive"
        )

        if (!managerIsActive) {
            println(
                "Manager account creation rejected"
            )

            println(
                "Reason: Manager account is inactive"
            )

            println(
                "=================================================="
            )

            return ManagerCreateUserResult
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

                return ManagerCreateUserResult
                    .Failed
            }

        println(
            "Authenticated account has Manager role: $accountIsManager"
        )

        if (!accountIsManager) {
            println(
                "Manager account creation rejected"
            )

            println(
                "Reason: Authenticated account is not a Manager"
            )

            println(
                "=================================================="
            )

            return ManagerCreateUserResult
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

                return ManagerCreateUserResult
                    .Failed
            }
                ?: run {
                    println(
                        "Manager account creation rejected"
                    )

                    println(
                        "Reason: Manager has no assigned region"
                    )

                    println(
                        "=================================================="
                    )

                    return ManagerCreateUserResult
                        .ManagerRegionNotAssigned
                }

        println(
            "Manager region ID: $managerRegionId"
        )

        val normalizedUserId =
            request.userId.trim()

        if (normalizedUserId.isBlank()) {
            println(
                "Manager account creation rejected"
            )

            println(
                "Reason: User ID is blank"
            )

            println(
                "=================================================="
            )

            return ManagerCreateUserResult
                .UserIdRequired
        }

        val normalizedPhoneNumber =
            request.phoneNumber
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }

        /*
         * The Manager cannot choose another region.
         *
         * The Manager cannot use this endpoint to
         * create another Manager or Admin.
         */
        val normalizedRequest =
            request.copy(
                userId =
                    normalizedUserId,

                phoneNumber =
                    normalizedPhoneNumber,

                regionId =
                    managerRegionId,

                role =
                    Role.Staff,

                isActive =
                    true,

                isStaff =
                    true,

                isSuperuser =
                    false
            )

        println(
            "Normalized account creation request"
        )

        println(
            "User ID: ${normalizedRequest.userId}"
        )

        println(
            "First name: ${normalizedRequest.firstName}"
        )

        println(
            "Middle name: ${normalizedRequest.middleName}"
        )

        println(
            "Last name: ${normalizedRequest.lastName}"
        )

        println(
            "Role: ${normalizedRequest.role}"
        )

        println(
            "Gender: ${normalizedRequest.gender}"
        )

        println(
            "Region ID: ${normalizedRequest.regionId}"
        )

        println(
            "District ID: ${normalizedRequest.districtId}"
        )

        println(
            "Directorate ID: ${normalizedRequest.directorateId}"
        )

        println(
            "Category ID: ${normalizedRequest.categoryId}"
        )

        println(
            "Phone number supplied: ${normalizedPhoneNumber != null}"
        )

        println(
            "Default password will be applied"
        )

        return try {
            val userIdAlreadyExists =
                ManagerCreateUserRepository
                    .userIdExists(
                        normalizedUserId
                    )

            println(
                "User ID already exists: $userIdAlreadyExists"
            )

            if (userIdAlreadyExists) {
                println(
                    "Manager account creation rejected"
                )

                println(
                    "Reason: User ID already exists"
                )

                println(
                    "=================================================="
                )

                return ManagerCreateUserResult
                    .UserIdExists
            }

            if (normalizedPhoneNumber != null) {
                val phoneNumberAlreadyExists =
                    ManagerCreateUserRepository
                        .phoneNumberExists(
                            normalizedPhoneNumber
                        )

                println(
                    "Phone number already exists: $phoneNumberAlreadyExists"
                )

                if (phoneNumberAlreadyExists) {
                    println(
                        "Manager account creation rejected"
                    )

                    println(
                        "Reason: Phone number already exists"
                    )

                    println(
                        "=================================================="
                    )

                    return ManagerCreateUserResult
                        .PhoneNumberExists
                }
            } else {
                println(
                    "No phone number was supplied"
                )
            }

            println(
                "Creating account in Manager region"
            )

            val createdAccountId =
                ManagerCreateUserRepository
                    .createAccount(
                        request =
                            normalizedRequest
                    )

            println(
                "Account inserted successfully"
            )

            println(
                "Created account ID: $createdAccountId"
            )

            val createdAccount =
                ManagerCreateUserRepository
                    .getAccountById(
                        accountId =
                            createdAccountId
                    )
                    ?: run {
                        println(
                            "Created account could not be retrieved"
                        )

                        println(
                            "Created account ID: $createdAccountId"
                        )

                        println(
                            "=================================================="
                        )

                        return ManagerCreateUserResult
                            .CreatedAccountNotFound
                    }

            println(
                "Created account retrieved successfully"
            )

            println(
                "Account ID: ${createdAccount.id}"
            )

            println(
                "User ID: ${createdAccount.userId}"
            )

            println(
                "Full name: ${createdAccount.fullName}"
            )

            println(
                "Role: ${createdAccount.role}"
            )

            println(
                "Region ID: ${createdAccount.regionId}"
            )

            println(
                "Region name: ${createdAccount.regionName}"
            )

            println(
                "Account active: ${createdAccount.isActive}"
            )

            println(
                "Manager account creation service completed successfully"
            )

            println(
                "=================================================="
            )

            ManagerCreateUserResult.Success(
                account =
                    createdAccount
            )
        } catch (exception: Exception) {
            println(
                "Manager account creation service failed"
            )

            println(
                "Manager account ID: $managerAccountId"
            )

            println(
                "Manager region ID: $managerRegionId"
            )

            println(
                "Requested user ID: $normalizedUserId"
            )

            printExceptionDetails(
                exception
            )

            println(
                "=================================================="
            )

            ManagerCreateUserResult.Failed
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