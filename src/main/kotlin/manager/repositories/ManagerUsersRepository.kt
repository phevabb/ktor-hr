package com.hr.manager.repositories

import com.hr.account.dtos.AccountResponse
import com.hr.account.dtos.Role
import com.hr.account.repositories.AccountRepository
import com.hr.account.table.Accounts
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagerUsersRepository {

    fun getAccountRole(
        row: ResultRow
    ): String {
        val accountRole =
            row[
                Accounts.role
            ]

        println(
            "Authenticated account database role: ${accountRole?.name ?: "Not assigned"}"
        )

        return accountRole
            ?.name
            ?: ""
    }


    suspend fun findManagerAccount(
        accountId: Int
    ): ResultRow? {
        println(
            "Finding Manager account: accountId=$accountId"
        )

        return Accounts
            .selectAll()
            .where {
                Accounts.id eq accountId
            }
            .firstOrNull()
    }

    fun isManager(
        row: ResultRow
    ): Boolean {
        val role =
            row[Accounts.role]

        println(
            "Authenticated account database role: $role"
        )

        return role ==
                Role.Manager
    }

    fun isActive(
        row: ResultRow
    ): Boolean {
        val active =
            row[Accounts.isActive]

        println(
            "Authenticated Manager active status: $active"
        )

        return active
    }

    fun getManagerRegionId(
        row: ResultRow
    ): Int? {
        val regionId =
            row[Accounts.regionId]
                ?.value

        println(
            "Authenticated Manager region ID: $regionId"
        )

        return regionId
    }

    suspend fun getActiveUsersInRegion(
        regionId: Int
    ): List<AccountResponse> {
        println(
            "Retrieving active accounts in Manager region"
        )

        println(
            "Manager region ID: $regionId"
        )

        val accountRows =
            Accounts
                .selectAll()
                .where {
                    (
                            Accounts.isActive eq true
                            ) and (
                            Accounts.regionId eq
                                    regionId
                            )
                }
                .toList()

        val sortedRows =
            accountRows.sortedWith(
                compareBy<ResultRow>(
                    {
                        it[Accounts.firstName]
                            ?.lowercase()
                            ?: ""
                    },
                    {
                        it[Accounts.lastName]
                            ?.lowercase()
                            ?: ""
                    },
                    {
                        it[Accounts.id]
                            .value
                    }
                )
            )

        val accounts =
            mutableListOf<AccountResponse>()

        for (row in sortedRows) {
            accounts.add(
                AccountRepository
                    .rowToAccountResponse(
                        row
                    )
            )
        }

        println(
            "Active Manager-region accounts retrieved: " +
                    "regionId=$regionId, " +
                    "count=${accounts.size}"
        )

        return accounts
    }

    suspend fun findActiveUserByUserId(
        userId: String
    ): ResultRow? {
        val normalizedUserId =
            userId.trim()

        println(
            "Finding active account by staff user ID"
        )

        println(
            "Requested user ID: $normalizedUserId"
        )

        return Accounts
            .selectAll()
            .where {
                (
                        Accounts.userId eq
                                normalizedUserId
                        ) and (
                        Accounts.isActive eq
                                true
                        )
            }
            .firstOrNull()
    }

    fun getAccountRegionId(
        row: ResultRow
    ): Int? {
        val regionId =
            row[Accounts.regionId]
                ?.value

        println(
            "Requested account region ID: $regionId"
        )

        return regionId
    }

    suspend fun mapAccountResponse(
        row: ResultRow
    ): AccountResponse {
        println(
            "Mapping requested Manager-region account"
        )

        return AccountRepository
            .rowToAccountResponse(
                row
            )
    }
}