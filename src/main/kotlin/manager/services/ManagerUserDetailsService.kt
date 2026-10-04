package com.hr.manager.services

import com.hr.manager.repositories.ManagerUsersRepository

object ManagerUserDetailsService {

    suspend fun getUserDetails(
        managerAccountId: Int,
        requestedUserId: String
    ): ManagerUserDetailsResult {
        println(
            "=================================================="
        )

        println(
            "Manager user-details service started"
        )

        println(
            "Authenticated Manager account ID: $managerAccountId"
        )

        println(
            "Requested staff user ID: $requestedUserId"
        )

        val normalizedUserId =
            requestedUserId.trim()

        if (normalizedUserId.isBlank()) {
            println(
                "Requested user ID is blank"
            )

            return ManagerUserDetailsResult
                .UserNotFound
        }

        val managerRow =
            ManagerUsersRepository
                .findManagerAccount(
                    managerAccountId
                )
                ?: run {
                    println(
                        "Authenticated Manager account was not found"
                    )

                    return ManagerUserDetailsResult
                        .ManagerAccountNotFound
                }

        if (
            !ManagerUsersRepository
                .isActive(
                    managerRow
                )
        ) {
            println(
                "Manager user-details request rejected because the Manager account is inactive"
            )

            return ManagerUserDetailsResult
                .ManagerAccountInactive
        }

        if (
            !ManagerUsersRepository
                .isManager(
                    managerRow
                )
        ) {
            println(
                "Manager user-details request rejected because the authenticated account is not a Manager"
            )

            return ManagerUserDetailsResult
                .AccessDenied
        }

        val managerRegionId =
            ManagerUsersRepository
                .getManagerRegionId(
                    managerRow
                )
                ?: run {
                    println(
                        "Manager user-details request rejected because the Manager has no assigned region"
                    )

                    return ManagerUserDetailsResult
                        .ManagerRegionNotAssigned
                }

        return try {
            val userRow =
                ManagerUsersRepository
                    .findActiveUserByUserId(
                        normalizedUserId
                    )
                    ?: run {
                        println(
                            "Requested active user was not found"
                        )

                        println(
                            "Requested user ID: $normalizedUserId"
                        )

                        return ManagerUserDetailsResult
                            .UserNotFound
                    }

            val userRegionId =
                ManagerUsersRepository
                    .getAccountRegionId(
                        userRow
                    )
                    ?: run {
                        println(
                            "Requested user does not have an assigned region"
                        )

                        return ManagerUserDetailsResult
                            .UserRegionNotAssigned
                    }

            println(
                "Comparing Manager region with requested user region"
            )

            println(
                "Manager region ID: $managerRegionId"
            )

            println(
                "Requested user region ID: $userRegionId"
            )

            if (
                managerRegionId !=
                userRegionId
            ) {
                println(
                    "Access rejected because the requested user is outside the Manager's region"
                )

                return ManagerUserDetailsResult
                    .UserOutsideManagerRegion
            }

            val account =
                ManagerUsersRepository
                    .mapAccountResponse(
                        userRow
                    )

            println(
                "Manager user details retrieved successfully"
            )

            println(
                "Requested account ID: ${account.id}"
            )

            println(
                "Requested user ID: ${account.userId}"
            )

            println(
                "Requested full name: ${account.fullName}"
            )

            println(
                "Requested role: ${account.role}"
            )

            println(
                "Requested region: ${account.regionName}"
            )

            println(
                "=================================================="
            )

            ManagerUserDetailsResult.Success(
                account
            )
        } catch (exception: Exception) {
            println(
                "Manager user-details service failed"
            )

            println(
                "Error type: ${exception::class.simpleName}"
            )

            println(
                "Error message: ${exception.message}"
            )

            println(
                "=================================================="
            )

            ManagerUserDetailsResult.Failed
        }
    }
}