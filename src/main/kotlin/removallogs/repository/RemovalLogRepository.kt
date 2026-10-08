package com.hr.removallogs.repository

import com.hr.account.table.Accounts
import com.hr.removallogs.service.RemovalLogResult
import com.hr.removallogs.table.UserRemovalLogs
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object RemovalLogRepository {

    suspend fun removeAccount(
        accountId: Int,
        removedByAccountId: Int,
        removedByRole: String,
        reason: String
    ): RemovalLogResult {
        println(
            "Shared account-removal repository operation started"
        )

        println(
            "Account being removed: $accountId"
        )

        println(
            "Removal requested by account: $removedByAccountId"
        )

        println(
            "Removal requested by role: $removedByRole"
        )

        if (
            accountId ==
            removedByAccountId
        ) {
            println(
                "Account removal rejected because an account cannot remove itself"
            )

            return RemovalLogResult
                .CannotRemoveOwnAccount
        }

        val accountEntityId =
            EntityID(
                id =
                    accountId,

                table =
                    Accounts
            )

        val actorEntityId =
            EntityID(
                id =
                    removedByAccountId,

                table =
                    Accounts
            )

        val actorExists =
            Accounts
                .selectAll()
                .where {
                    Accounts.id eq
                            actorEntityId
                }
                .limit(
                    1
                )
                .firstOrNull() !=
                    null

        if (!actorExists) {
            println(
                "Removal actor account was not found"
            )

            return RemovalLogResult
                .ActorAccountNotFound
        }

        val accountRow =
            Accounts
                .selectAll()
                .where {
                    Accounts.id eq
                            accountEntityId
                }
                .limit(
                    1
                )
                .firstOrNull()
                ?: run {
                    println(
                        "Account selected for removal was not found"
                    )

                    return RemovalLogResult
                        .AccountNotFound
                }

        val accountIsActive =
            accountRow[
                Accounts.isActive
            ]

        println(
            "Account currently active: $accountIsActive"
        )

        if (!accountIsActive) {
            return RemovalLogResult
                .AlreadyInactive
        }

        val updatedRows =
            Accounts.update(
                where = {
                    (
                            Accounts.id eq
                                    accountEntityId
                            ) and
                            (
                                    Accounts.isActive eq
                                            true
                                    )
                }
            ) {
                it[
                    Accounts.isActive
                ] =
                    false
            }

        println(
            "Account rows updated: $updatedRows"
        )

        if (updatedRows != 1) {
            return RemovalLogResult
                .UpdateFailed
        }

        UserRemovalLogs.insert {
            it[
                UserRemovalLogs.accountId
            ] =
                accountEntityId

            it[
                UserRemovalLogs.removedByAccountId
            ] =
                actorEntityId

            it[
                UserRemovalLogs.removedByRole
            ] =
                removedByRole

            it[
                UserRemovalLogs.reason
            ] =
                reason
        }

        println(
            "Removal log inserted successfully"
        )

        return RemovalLogResult
            .Success
    }
}