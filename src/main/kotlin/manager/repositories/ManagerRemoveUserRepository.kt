package com.hr.manager.repositories

import com.hr.account.table.Accounts
import com.hr.manager.table.UserRemovalLogs
import java.time.LocalDateTime
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object ManagerRemoveUserRepository {

    suspend fun removeUser(
        accountId: Int,
        managerRegionId: Int,
        reason: String
    ): ManagerRemoveUserRepositoryResult {
        println(
            "Finding account selected for removal"
        )

        println(
            "Target account ID: $accountId"
        )

        val accountRow =
            Accounts
                .selectAll()
                .where {
                    Accounts.id eq
                            accountId
                }
                .firstOrNull()
                ?: run {
                    println(
                        "Target account was not found"
                    )

                    return ManagerRemoveUserRepositoryResult
                        .UserNotFound
                }

        val targetUserId =
            accountRow[
                Accounts.userId
            ]
                ?: ""

        val targetIsActive =
            accountRow[
                Accounts.isActive
            ]

        val targetRegionId =
            accountRow[
                Accounts.regionId
            ]
                ?.value

        println(
            "Target user ID: $targetUserId"
        )

        println(
            "Target account active: $targetIsActive"
        )

        println(
            "Target account region ID: $targetRegionId"
        )

        println(
            "Manager region ID: $managerRegionId"
        )

        if (!targetIsActive) {
            println(
                "Target account is already inactive"
            )

            return ManagerRemoveUserRepositoryResult
                .UserAlreadyInactive
        }

        if (targetRegionId == null) {
            println(
                "Target account does not have an assigned region"
            )

            return ManagerRemoveUserRepositoryResult
                .UserRegionNotAssigned
        }

        if (
            targetRegionId !=
            managerRegionId
        ) {
            println(
                "Target account is outside the Manager region"
            )

            return ManagerRemoveUserRepositoryResult
                .UserOutsideManagerRegion
        }

        val removedAt =
            LocalDateTime.now()

        println(
            "Deactivating target account"
        )

        val updatedRows =
            Accounts.update(
                where = {
                    Accounts.id eq
                            accountId
                }
            ) {
                it[Accounts.isActive] =
                    false
            }

        println(
            "Account rows updated: $updatedRows"
        )

        if (updatedRows != 1) {
            println(
                "Account deactivation failed"
            )

            return ManagerRemoveUserRepositoryResult
                .Failed
        }

        println(
            "Creating account removal log"
        )

        UserRemovalLogs.insert {
            it[UserRemovalLogs.accountId] =
                EntityID(
                    accountId,
                    Accounts
                )

            it[UserRemovalLogs.reason] =
                reason

            it[UserRemovalLogs.removedAt] =
                removedAt
        }

        println(
            "Account removal log created successfully"
        )

        println(
            "Removed account ID: $accountId"
        )

        println(
            "Removed user ID: $targetUserId"
        )

        println(
            "Removed at: $removedAt"
        )

        return ManagerRemoveUserRepositoryResult.Success(
            accountId =
                accountId,

            userId =
                targetUserId,

            removedAt =
                removedAt
        )
    }
}