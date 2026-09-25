package com.hr.admin.services


import com.hr.admin.repositories.AdminDashboardRepository
import com.hr.auth.repositories.AuthRepository

object AdminDashboardService {

    suspend fun getDashboardSummary(
        accountId: Int
    ): AdminDashboardResult {
        val account =
            AuthRepository
                .findAccountById(
                    accountId
                )
                ?: return AdminDashboardResult
                    .AccountNotFound

        if (!account.isActive) {
            return AdminDashboardResult
                .AccountInactive
        }

        val isAdmin =
            account.role.equals(
                other = "Admin",
                ignoreCase = true
            )

        if (!isAdmin) {
            println(
                "Admin dashboard access denied: " +
                        "accountId=${account.id}, " +
                        "userId=${account.userId}, " +
                        "role=${account.role}"
            )

            return AdminDashboardResult
                .AccessDenied
        }

        return try {
            val summary =
                AdminDashboardRepository
                    .getDashboardSummary()

            println(
                "Admin dashboard summary retrieved: " +
                        "accountId=${account.id}, " +
                        "activeUsers=${summary.numOfUsers}"
            )

            AdminDashboardResult.Success(
                summary = summary
            )
        } catch (exception: Exception) {
            println(
                "Admin dashboard summary failed: " +
                        "accountId=${account.id}, " +
                        "errorType=${exception::class.simpleName}, " +
                        "message=${exception.message}"
            )

            AdminDashboardResult.Failed
        }
    }
}
