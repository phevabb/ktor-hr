package com.hr.manager.services

import com.hr.manager.repositories.ManagerDashboardRepository

object ManagerDashboardService {

    suspend fun getDashboardSummary(
        managerAccountId: Int
    ): ManagerDashboardResult {
        println(
            "=================================================="
        )

        println(
            "Manager dashboard summary service started"
        )

        println(
            "Authenticated Manager account ID: $managerAccountId"
        )

        val managerRow =
            ManagerDashboardRepository
                .findManagerAccount(
                    managerAccountId
                )
                ?: run {
                    println(
                        "Authenticated Manager account was not found"
                    )

                    return ManagerDashboardResult
                        .AccountNotFound
                }

        if (
            !ManagerDashboardRepository
                .isActive(
                    managerRow
                )
        ) {
            println(
                "Manager dashboard request rejected because the account is inactive"
            )

            return ManagerDashboardResult
                .AccountInactive
        }

        if (
            !ManagerDashboardRepository
                .isManager(
                    managerRow
                )
        ) {
            println(
                "Manager dashboard request rejected because the database account is not a Manager"
            )

            return ManagerDashboardResult
                .AccessDenied
        }

        val managerRegionId =
            ManagerDashboardRepository
                .getRegionId(
                    managerRow
                )
                ?: run {
                    println(
                        "Manager dashboard request rejected because the Manager has no assigned region"
                    )

                    return ManagerDashboardResult
                        .RegionNotAssigned
                }

        return try {
            val summary =
                ManagerDashboardRepository
                    .getSummaryForRegion(
                        managerRegionId
                    )

            println(
                "Manager dashboard summary service completed successfully"
            )

            println(
                "Manager account ID: $managerAccountId"
            )

            println(
                "Manager region ID: $managerRegionId"
            )

            println(
                "=================================================="
            )

            ManagerDashboardResult.Success(
                summary
            )
        } catch (exception: Exception) {
            println(
                "Manager dashboard summary service failed"
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

            ManagerDashboardResult.Failed
        }
    }
}