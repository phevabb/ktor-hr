package com.hr.manager.services


import com.hr.manager.repositories.ManagerUsersExcelRepository
import com.hr.manager.repositories.ManagerUsersRepository

object ManagerUsersExcelService {

    suspend fun getAllUsersForExcel(
        managerAccountId: Int
    ): ManagerUsersExcelResult {
        println(
            "=================================================="
        )

        println(
            "Manager Excel export service started"
        )

        println(
            "Authenticated account ID: $managerAccountId"
        )

        val managerRow =
            try {
                ManagerUsersRepository
                    .findManagerAccount(
                        accountId =
                            managerAccountId
                    )
            } catch (exception: Exception) {
                println(
                    "Failed to retrieve the authenticated Manager account"
                )

                println(
                    "Account ID: $managerAccountId"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
                )

                exception.cause?.let { cause ->
                    println(
                        "Cause type: ${cause::class.simpleName}"
                    )

                    println(
                        "Cause message: ${cause.message}"
                    )
                }

                println(
                    "=================================================="
                )

                return ManagerUsersExcelResult
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

                    return ManagerUsersExcelResult
                        .AccountNotFound
                }

        println(
            "Authenticated account was found"
        )

        val managerIsActive =
            try {
                ManagerUsersRepository
                    .isActive(
                        managerRow
                    )
            } catch (exception: Exception) {
                println(
                    "Failed to check Manager account active status"
                )

                println(
                    "Account ID: $managerAccountId"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
                )

                exception.cause?.let { cause ->
                    println(
                        "Cause type: ${cause::class.simpleName}"
                    )

                    println(
                        "Cause message: ${cause.message}"
                    )
                }

                println(
                    "=================================================="
                )

                return ManagerUsersExcelResult
                    .Failed
            }

        println(
            "Manager account active: $managerIsActive"
        )

        if (!managerIsActive) {
            println(
                "Manager Excel export request rejected"
            )

            println(
                "Reason: Manager account is inactive"
            )

            println(
                "Account ID: $managerAccountId"
            )

            println(
                "=================================================="
            )

            return ManagerUsersExcelResult
                .AccountInactive
        }

        val accountIsManager =
            try {
                ManagerUsersRepository
                    .isManager(
                        managerRow
                    )
            } catch (exception: Exception) {
                println(
                    "Failed to check authenticated account role"
                )

                println(
                    "Account ID: $managerAccountId"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
                )

                exception.cause?.let { cause ->
                    println(
                        "Cause type: ${cause::class.simpleName}"
                    )

                    println(
                        "Cause message: ${cause.message}"
                    )
                }

                println(
                    "=================================================="
                )

                return ManagerUsersExcelResult
                    .Failed
            }

        println(
            "Account has Manager role: $accountIsManager"
        )

        if (!accountIsManager) {
            println(
                "Manager Excel export request rejected"
            )

            println(
                "Reason: Authenticated account does not have the Manager role"
            )

            println(
                "Account ID: $managerAccountId"
            )

            println(
                "=================================================="
            )

            return ManagerUsersExcelResult
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
                    "Failed to retrieve Manager region ID"
                )

                println(
                    "Account ID: $managerAccountId"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
                )

                exception.cause?.let { cause ->
                    println(
                        "Cause type: ${cause::class.simpleName}"
                    )

                    println(
                        "Cause message: ${cause.message}"
                    )
                }

                println(
                    "=================================================="
                )

                return ManagerUsersExcelResult
                    .Failed
            }
                ?: run {
                    println(
                        "Manager Excel export request rejected"
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

                    return ManagerUsersExcelResult
                        .RegionNotAssigned
                }

        println(
            "Manager region ID: $managerRegionId"
        )

        return try {
            println(
                "Retrieving active users for Manager Excel export"
            )

            println(
                "Filtering accounts using region ID: $managerRegionId"
            )

            val users =
                ManagerUsersExcelRepository
                    .getActiveUsersForExcel(
                        regionId =
                            managerRegionId
                    )

            println(
                "Manager Excel export records retrieved successfully"
            )

            println(
                "Manager account ID: $managerAccountId"
            )

            println(
                "Manager region ID: $managerRegionId"
            )

            println(
                "Export record count: ${users.size}"
            )

            if (users.isEmpty()) {
                println(
                    "No active accounts were found in the Manager region"
                )
            } else {
                users.forEachIndexed {
                        index,
                        user ->

                    println(
                        "Export record ${index + 1}"
                    )

                    println(
                        "Account ID: ${user.id}"
                    )

                    println(
                        "User ID: ${user.userId}"
                    )

                    println(
                        "Full name: ${user.fullName}"
                    )

                    println(
                        "Role: ${user.role}"
                    )

                    println(
                        "Region: ${user.regionName}"
                    )
                }
            }

            println(
                "Manager Excel export service completed successfully"
            )

            println(
                "=================================================="
            )

            ManagerUsersExcelResult.Success(
                users =
                    users
            )
        } catch (exception: Exception) {
            println(
                "Manager Excel export service failed"
            )

            println(
                "Manager account ID: $managerAccountId"
            )

            println(
                "Manager region ID: $managerRegionId"
            )

            println(
                "Error type: ${exception::class.simpleName}"
            )

            println(
                "Error message: ${exception.message}"
            )

            exception.cause?.let { cause ->
                println(
                    "Cause type: ${cause::class.simpleName}"
                )

                println(
                    "Cause message: ${cause.message}"
                )
            }

            println(
                "=================================================="
            )

            ManagerUsersExcelResult.Failed
        }
    }
}