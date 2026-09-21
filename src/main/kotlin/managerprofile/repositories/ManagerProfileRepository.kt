package com.hr.managerprofile.repositories

import com.hr.account.helpers.AccountComputedValues
import com.hr.account.table.Accounts
import com.hr.managerprofile.dtos.ManagerProfileResponse
import com.hr.managerprofile.table.ManagerProfiles
import com.hr.region.tables.Regions
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object ManagerProfileRepository {


    suspend fun deleteManagerProfile(
        id: Int
    ): Boolean {
        val deletedRows =
            ManagerProfiles.deleteWhere {
                ManagerProfiles.id eq id
            }

        return deletedRows > 0
    }


    private fun managerProfileJoin() =
        ManagerProfiles
            .join(
                otherTable = Accounts,
                joinType = JoinType.INNER,
                onColumn = ManagerProfiles.accountId,
                otherColumn = Accounts.id
            )
            .join(
                otherTable = Regions,
                joinType = JoinType.INNER,
                onColumn = ManagerProfiles.regionId,
                otherColumn = Regions.id
            )

    private fun rowToManagerProfileResponse(
        row: ResultRow
    ): ManagerProfileResponse {
        val firstName =
            row[Accounts.firstName]

        val middleName =
            row[Accounts.middleName]

        val lastName =
            row[Accounts.lastName]

        val userId =
            row[Accounts.userId]

        val fullName =
            AccountComputedValues.fullName(
                firstName = firstName,
                middleName = middleName,
                lastName = lastName
            )

        val displayName =
            AccountComputedValues.displayName(
                fullName = fullName,
                userId = userId
            )

        return ManagerProfileResponse(
            id =
                row[ManagerProfiles.id].value,

            accountId =
                row[
                    ManagerProfiles.accountId
                ].value,

            userId =
                userId,

            fullName =
                fullName,

            displayName =
                displayName,

            role =
                row[Accounts.role],

            isActive =
                row[Accounts.isActive],

            isStaff =
                row[Accounts.isStaff],

            isSuperuser =
                row[Accounts.isSuperuser],

            regionId =
                row[
                    ManagerProfiles.regionId
                ].value,

            regionName =
                row[Regions.region]
        )
    }

    suspend fun getAll():
            List<ManagerProfileResponse> {
        return managerProfileJoin()
            .selectAll()
            .orderBy(
                ManagerProfiles.id,
                SortOrder.DESC
            )
            .map { row ->
                rowToManagerProfileResponse(
                    row
                )
            }
            .toList()
    }

    suspend fun getById(
        id: Int
    ): ManagerProfileResponse? {
        return managerProfileJoin()
            .selectAll()
            .where {
                ManagerProfiles.id eq id
            }
            .singleOrNull()
            ?.let { row ->
                rowToManagerProfileResponse(
                    row
                )
            }
    }

    suspend fun getByAccountId(
        accountId: Int
    ): ManagerProfileResponse? {
        return managerProfileJoin()
            .selectAll()
            .where {
                ManagerProfiles.accountId eq
                        accountId
            }
            .singleOrNull()
            ?.let { row ->
                rowToManagerProfileResponse(
                    row
                )
            }
    }

    suspend fun getByRegionId(
        regionId: Int
    ): List<ManagerProfileResponse> {
        return managerProfileJoin()
            .selectAll()
            .where {
                ManagerProfiles.regionId eq
                        regionId
            }
            .orderBy(
                ManagerProfiles.id,
                SortOrder.DESC
            )
            .map { row ->
                rowToManagerProfileResponse(
                    row
                )
            }
            .toList()
    }

    suspend fun accountExists(
        accountId: Int
    ): Boolean {
        return Accounts
            .selectAll()
            .where {
                Accounts.id eq accountId
            }
            .singleOrNull() != null
    }

    suspend fun regionExists(
        regionId: Int
    ): Boolean {
        return Regions
            .selectAll()
            .where {
                Regions.id eq regionId
            }
            .singleOrNull() != null
    }

    suspend fun managerProfileExists(
        id: Int
    ): Boolean {
        return ManagerProfiles
            .selectAll()
            .where {
                ManagerProfiles.id eq id
            }
            .singleOrNull() != null
    }

    suspend fun accountAlreadyAssigned(
        accountId: Int,
        excludeManagerProfileId: Int? = null
    ): Boolean {
        return ManagerProfiles
            .selectAll()
            .where {
                if (
                    excludeManagerProfileId == null
                ) {
                    ManagerProfiles.accountId eq
                            accountId
                } else {
                    (
                            ManagerProfiles.accountId eq
                                    accountId
                            ) and (
                            ManagerProfiles.id neq
                                    excludeManagerProfileId
                            )
                }
            }
            .singleOrNull() != null
    }

    suspend fun create(
        accountId: Int,
        regionId: Int
    ): Int {
        return ManagerProfiles
            .insertAndGetId {
                it[
                    ManagerProfiles.accountId
                ] = EntityID(
                    accountId,
                    Accounts
                )

                it[
                    ManagerProfiles.regionId
                ] = EntityID(
                    regionId,
                    Regions
                )
            }
            .value
    }

    suspend fun update(
        id: Int,
        accountId: Int,
        regionId: Int
    ): Boolean {
        return ManagerProfiles.update(
            where = {
                ManagerProfiles.id eq id
            }
        ) {
            it[ManagerProfiles.accountId] =
                EntityID(
                    accountId,
                    Accounts
                )

            it[ManagerProfiles.regionId] =
                EntityID(
                    regionId,
                    Regions
                )
        } > 0
    }}
