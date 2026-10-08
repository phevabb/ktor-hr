package com.hr.removallogs.service

import com.hr.removallogs.repository.RemovalLogRepository

sealed interface RemovalAuthorizationResult {

    data class Authorized(
        val normalizedRole: String
    ) :
        RemovalAuthorizationResult

    data object Forbidden :
        RemovalAuthorizationResult
}

object RemovalLogService {

    private val allowedRoles =
        mapOf(
            "admin" to
                    "Admin",

            "manager" to
                    "Manager",

            "superadmin" to
                    "SuperAdmin",

            "super_admin" to
                    "SuperAdmin",

            "super admin" to
                    "SuperAdmin"
        )

    private val allowedReasons =
        mapOf(
            "resigned" to
                    "Resigned",

            "retired" to
                    "Retired",

            "terminated" to
                    "Terminated",

            "other" to
                    "Other"
        )

    fun authorizeRole(
        role: String
    ): RemovalAuthorizationResult {
        val normalizedInput =
            role
                .trim()
                .lowercase()

        val normalizedRole =
            allowedRoles[
                normalizedInput
            ]
                ?: return RemovalAuthorizationResult
                    .Forbidden

        return RemovalAuthorizationResult
            .Authorized(
                normalizedRole =
                    normalizedRole
            )
    }

    fun normalizeReason(
        reason: String?
    ): String? {
        val normalizedInput =
            reason
                ?.trim()
                ?.lowercase()
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: return null

        return allowedReasons[
            normalizedInput
        ]
    }

    suspend fun removeAccount(
        accountId: Int,
        removedByAccountId: Int,
        removedByRole: String,
        reason: String
    ): RemovalLogResult {
        return RemovalLogRepository
            .removeAccount(
                accountId =
                    accountId,

                removedByAccountId =
                    removedByAccountId,

                removedByRole =
                    removedByRole,

                reason =
                    reason
            )
    }
}