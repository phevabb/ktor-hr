package com.hr.admin.repositories

import com.hr.account.table.Accounts
import com.hr.admin.dtos.ManagementUnitStatResponse
import com.hr.managementUnits.tables.ManagementUnits
import com.hr.region.tables.Regions
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagementUnitStatsRepository {

    suspend fun getAll(
        regionName: String?
    ): List<ManagementUnitStatResponse> {
        val normalizedRegionName =
            regionName
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }

        val managementUnitRows =
            ManagementUnits
                .selectAll()
                .orderBy(
                    ManagementUnits.managementUnitName,
                    SortOrder.ASC
                )
                .toList()

        val managementUnitCounts =
            managementUnitRows
                .associate { row ->
                    row[ManagementUnits.id].value to 0L
                }
                .toMutableMap()

        val filteredRegionId =
            findRegionIdByName(
                normalizedRegionName
            )

        var activeUsersCount =
            0L

        var groupedUsersCount =
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

                val belongsToSelectedRegion =
                    when {
                        normalizedRegionName == null -> {
                            true
                        }

                        filteredRegionId == null -> {
                            false
                        }

                        else -> {
                            accountRegionId ==
                                    filteredRegionId
                        }
                    }

                if (!belongsToSelectedRegion) {
                    return@collect
                }

                activeUsersCount += 1L

                val managementUnitId =
                    row[
                        Accounts.managementUnitCostCentreId
                    ]
                        ?.value

                if (
                    managementUnitId != null &&
                    managementUnitCounts.containsKey(
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

                    groupedUsersCount += 1L
                }
            }

        val statistics =
            managementUnitRows.map { row ->
                val managementUnitId =
                    row[ManagementUnits.id].value

                val managementUnitName =
                    row[
                        ManagementUnits.managementUnitName
                    ]

                ManagementUnitStatResponse(
                    managementUnit =
                        managementUnitName,

                    count =
                        managementUnitCounts[
                            managementUnitId
                        ] ?: 0L
                )
            }

        println(
            """
            Management unit statistics summary:
            Region filter: ${normalizedRegionName ?: "All regions"}
            Filtered region ID: ${filteredRegionId ?: "Not applicable"}
            Active users after region filter: $activeUsersCount
            Active users with management units: $groupedUsersCount
            Management units returned: ${statistics.size}
            Results: $statistics
            """.trimIndent()
        )

        return statistics
    }

    private suspend fun findRegionIdByName(
        regionName: String?
    ): Int? {
        if (regionName == null) {
            return null
        }

        val regionRows =
            Regions
                .selectAll()
                .toList()

        return regionRows
            .firstOrNull { row ->
                row[Regions.region]
                    .trim()
                    .equals(
                        other = regionName,
                        ignoreCase = true
                    )
            }
            ?.get(Regions.id)
            ?.value
    }
}