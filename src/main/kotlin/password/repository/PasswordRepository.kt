package com.hr.password.repository

import com.hr.account.table.Accounts
import com.hr.password.security.PasswordSecurity
import com.hr.password.table.PasswordResetTokens
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update
import java.time.LocalDateTime

data class PasswordAccountIdentity(
    val id: Int,
    val email: String,
    val fullName: String,
    val passwordHash: String
)

data class ValidResetToken(
    val tokenId: Int,
    val accountId: Int
)

object PasswordRepository {

    suspend fun findActiveAccountByEmail(
        email: String
    ): PasswordAccountIdentity? {
        val row =
            Accounts
                .selectAll()
                .where {
                    (
                            Accounts.email eq email
                            ) and
                            (
                                    Accounts.isActive eq true
                                    )
                }
                .limit(
                    1
                )
                .toList()
                .firstOrNull()
                ?: return null

        return rowToIdentity(
            row
        )
    }

    suspend fun findActiveAccountById(
        accountId: Int
    ): PasswordAccountIdentity? {
        val row =
            Accounts
                .selectAll()
                .where {
                    (
                            Accounts.id eq accountId
                            ) and
                            (
                                    Accounts.isActive eq true
                                    )
                }
                .limit(
                    1
                )
                .toList()
                .firstOrNull()
                ?: return null

        return rowToIdentity(
            row
        )
    }

    suspend fun createResetToken(
        accountId: Int,
        tokenHash: String,
        expiresAt: LocalDateTime
    ) {
        val now =
            LocalDateTime.now()

        PasswordResetTokens.update(
            where = {
                (
                        PasswordResetTokens.accountId eq accountId
                        ) and
                        PasswordResetTokens.usedAt.isNull()
            }
        ) {
            it[
                PasswordResetTokens.usedAt
            ] =
                now
        }

        PasswordResetTokens.insert {
            it[
                PasswordResetTokens.accountId
            ] =
                accountId

            it[
                PasswordResetTokens.tokenHash
            ] =
                tokenHash

            it[
                PasswordResetTokens.expiresAt
            ] =
                expiresAt
        }
    }

    suspend fun findValidResetToken(
        rawToken: String
    ): ValidResetToken? {
        val tokenHash =
            PasswordSecurity.hashResetToken(
                rawToken
            )

        val row =
            PasswordResetTokens
                .selectAll()
                .where {
                    (
                            PasswordResetTokens.tokenHash eq tokenHash
                            ) and
                            PasswordResetTokens.usedAt.isNull()
                }
                .limit(
                    1
                )
                .toList()
                .firstOrNull()
                ?: return null

        val expiresAt =
            row[
                PasswordResetTokens.expiresAt
            ]

        if (
            !expiresAt.isAfter(
                LocalDateTime.now()
            )
        ) {
            return null
        }

        return ValidResetToken(
            tokenId =
                row[
                    PasswordResetTokens.id
                ].value,

            accountId =
                row[
                    PasswordResetTokens.accountId
                ].value
        )
    }

    suspend fun updatePasswordAndUseToken(
        accountId: Int,
        tokenId: Int,
        newPasswordHash: String
    ): Boolean {
        val updatedAccounts =
            Accounts.update(
                where = {
                    (
                            Accounts.id eq accountId
                            ) and
                            (
                                    Accounts.isActive eq true
                                    )
                }
            ) {
                it[
                    Accounts.passwordHash
                ] =
                    newPasswordHash
            }

        if (
            updatedAccounts != 1
        ) {
            return false
        }

        val updatedTokens =
            PasswordResetTokens.update(
                where = {
                    (
                            PasswordResetTokens.id eq tokenId
                            ) and
                            PasswordResetTokens.usedAt.isNull()
                }
            ) {
                it[
                    PasswordResetTokens.usedAt
                ] =
                    LocalDateTime.now()
            }

        return updatedTokens == 1
    }

    suspend fun changePassword(
        accountId: Int,
        newPasswordHash: String
    ): Boolean {
        val updatedRows =
            Accounts.update(
                where = {
                    (
                            Accounts.id eq accountId
                            ) and
                            (
                                    Accounts.isActive eq true
                                    )
                }
            ) {
                it[
                    Accounts.passwordHash
                ] =
                    newPasswordHash
            }

        if (
            updatedRows == 1
        ) {
            PasswordResetTokens.update(
                where = {
                    (
                            PasswordResetTokens.accountId eq accountId
                            ) and
                            PasswordResetTokens.usedAt.isNull()
                }
            ) {
                it[
                    PasswordResetTokens.usedAt
                ] =
                    LocalDateTime.now()
            }
        }

        return updatedRows == 1
    }

    suspend fun deleteExpiredTokens() {
        PasswordResetTokens.deleteWhere {
            PasswordResetTokens.expiresAt lessEq
                    LocalDateTime.now()
        }
    }

    private fun rowToIdentity(
        row: ResultRow
    ): PasswordAccountIdentity {
        val accountId =
            row[
                Accounts.id
            ].value

        val userId =
            row[
                Accounts.userId
            ]

        val firstName =
            row[
                Accounts.firstName
            ]

        val middleName =
            row[
                Accounts.middleName
            ]

        val lastName =
            row[
                Accounts.lastName
            ]

        val fullName =
            listOf(
                firstName,
                middleName,
                lastName
            )
                .mapNotNull { value ->
                    value
                        ?.trim()
                        ?.takeIf {
                            it.isNotEmpty()
                        }
                }
                .joinToString(
                    separator = " "
                )
                .ifBlank {
                    userId
                        ?.trim()
                        ?.takeIf {
                            it.isNotEmpty()
                        }
                        ?: "HR User"
                }

        val email =
            row[
                Accounts.email
            ]
                ?.trim()
                ?.takeIf {
                    it.isNotEmpty()
                }
                ?: throw IllegalStateException(
                    "Account $accountId has no email address."
                )

        return PasswordAccountIdentity(
            id =
                accountId,

            email =
                email,

            fullName =
                fullName,

            passwordHash =
                row[
                    Accounts.passwordHash
                ]
        )
    }
}