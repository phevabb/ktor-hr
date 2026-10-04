package com.hr.manager.repositories

import com.hr.account.table.Accounts
import com.hr.managementUnits.tables.ManagementUnits
import com.hr.manager.dtos.ManagerManagementStatResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagerManagementStatsRepository {

    suspend fun getManagementStatsForRegion(
        regionId: Int
    ): List<ManagerManagementStatResponse> {
        println(
            "Retrieving management-unit statistics for Manager region"
        )

        println(
            "Manager region ID: $regionId"
        )

        val managementUnitRows =
            ManagementUnits
                .selectAll()
                .orderBy(
                    ManagementUnits.managementUnitName,
                    SortOrder.ASC
                )
                .toList()

        println(
            "Management units retrieved: ${managementUnitRows.size}"
        )

        val managementUnitCounts =
            managementUnitRows
                .associate { row ->
                    row[
                        ManagementUnits.id
                    ].value to 0L
                }
                .toMutableMap()

        var activeAccountsInRegion =
            0L

        var accountsWithManagementUnit =
            0L

        Accounts
            .selectAll()
            .where {
                Accounts.isActive eq true
            }
            .collect { row ->
                val accountRegionId =
                    row[Accounts.regionId]
                        ?.value

                if (
                    accountRegionId !=
                    regionId
                ) {
                    return@collect
                }

                activeAccountsInRegion += 1L

                val managementUnitId =
                    row[
                        Accounts
                            .managementUnitCostCentreId
                    ]
                        ?.value

                if (
                    managementUnitId != null &&
                    managementUnitCounts
                        .containsKey(
                            managementUnitId
                        )
                ) {
                    val currentCount =
                        managementUnitCounts[
                            managementUnitId
                        ] ?: 0L

                    managementUnitCounts[
                        managementUnitId
                    ] = currentCount + 1L

                    accountsWithManagementUnit +=
                        1L
                }
            }

        val statistics =
            managementUnitRows.map { row ->
                val managementUnitId =
                    row[
                        ManagementUnits.id
                    ].value

                val managementUnitName =
                    row[
                        ManagementUnits
                            .managementUnitName
                    ]

                ManagerManagementStatResponse(
                    managementUnit =
                        managementUnitName,

                    count =
                        managementUnitCounts[
                            managementUnitId
                        ] ?: 0L
                )
            }

        println(
            "Manager management-unit statistics completed"
        )

        println(
            "Region ID: $regionId"
        )

        println(
            "Active accounts in region: $activeAccountsInRegion"
        )

        println(
            "Accounts with management unit: $accountsWithManagementUnit"
        )

        println(
            "Management-unit statistic records: ${statistics.size}"
        )

        statistics.forEach { statistic ->
            println(
                "Management unit: ${statistic.managementUnit}, " +
                        "count=${statistic.count}"
            )
        }

        return statistics
    }
}