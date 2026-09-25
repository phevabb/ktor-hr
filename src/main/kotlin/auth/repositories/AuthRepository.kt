package com.hr.auth.repositories

import com.hr.account.table.Accounts
import com.hr.managerprofile.table.ManagerProfiles
import com.hr.region.tables.Regions
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object AuthRepository {

    suspend fun findAccountByUserId(
        userId: String
    ): AuthAccountRecord? {
        val row =
            Accounts
                .selectAll()
                .where {
                    Accounts.userId eq userId
                }
                .singleOrNull()
                ?: return null

        return rowToAuthAccountRecord(row)
    }

    suspend fun findAccountById(
        accountId: Int
    ): AuthAccountRecord? {
        val row =
            Accounts
                .selectAll()
                .where {
                    Accounts.id eq accountId
                }
                .singleOrNull()
                ?: return null

        return rowToAuthAccountRecord(row)
    }

    suspend fun findManagerRegion(
        accountId: Int
    ): ManagerRegionRecord? {
        val join =
            ManagerProfiles
                .join(
                    otherTable = Regions,
                    joinType = JoinType.INNER,
                    onColumn =
                        ManagerProfiles.regionId,
                    otherColumn =
                        Regions.id
                )

        val row =
            join
                .selectAll()
                .where {
                    ManagerProfiles.accountId eq
                            accountId
                }
                .singleOrNull()
                ?: return null

        return ManagerRegionRecord(
            regionId =
                row[
                    ManagerProfiles.regionId
                ].value,

            regionName =
                row[Regions.region]
        )
    }

    suspend fun updatePassword(
        accountId: Int,
        passwordHash: String
    ): Boolean {
        val updatedRows =
            Accounts.update(
                where = {
                    Accounts.id eq accountId
                }
            ) {
                it[
                    Accounts.passwordHash
                ] = passwordHash
            }

        return updatedRows > 0
    }

    private fun rowToAuthAccountRecord(
        row: ResultRow
    ): AuthAccountRecord {
        val firstName =
            row[Accounts.firstName]

        val middleName =
            row[Accounts.middleName]

        val lastName =
            row[Accounts.lastName]

        val fullName =
            listOfNotNull(
                firstName,
                middleName,
                lastName
            )
                .map {
                    it.trim()
                }
                .filter {
                    it.isNotBlank()
                }
                .joinToString(" ")

        val userId =
            row[Accounts.userId]

        val displayName =
            when {
                fullName.isNotBlank() &&
                        !userId.isNullOrBlank() -> {
                    "$fullName - $userId"
                }

                fullName.isNotBlank() -> {
                    fullName
                }

                !userId.isNullOrBlank() -> {
                    userId
                }

                else -> {
                    "Account #${row[Accounts.id].value}"
                }
            }

        return AuthAccountRecord(
            id =
                row[Accounts.id].value,

            userId =
                userId,

            passwordHash =
                row[Accounts.passwordHash],

            role =
                row[Accounts.role]
                    .toString(),

            fullName =
                fullName,

            displayName =
                displayName,

            isActive =
                row[Accounts.isActive],

            isStaff =
                row[Accounts.isStaff],

            isSuperuser =
                row[Accounts.isSuperuser]
        )
    }
}

data class AuthAccountRecord(
    val id: Int,
    val userId: String?,
    val passwordHash: String,
    val role: String,
    val fullName: String,
    val displayName: String,
    val isActive: Boolean,
    val isStaff: Boolean,
    val isSuperuser: Boolean
)

data class ManagerRegionRecord(
    val regionId: Int,
    val regionName: String
)