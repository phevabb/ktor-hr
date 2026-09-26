package com.hr.admin.repositories

import com.hr.account.table.Accounts
import com.hr.admin.dtos.RegionStatResponse
import com.hr.region.tables.Regions
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object RegionStatsRepository {

    suspend fun getAll():
            List<RegionStatResponse> {
        val regionRows =
            Regions
                .selectAll()
                .orderBy(
                    Regions.region,
                    SortOrder.ASC
                )
                .toList()

        val regionNamesById =
            regionRows.associate { row ->
                val regionId =
                    row[Regions.id].value

                val regionName =
                    row[Regions.region]

                regionId to regionName
            }

        val regionCounts =
            regionNamesById.keys
                .associateWith {
                    0L
                }
                .toMutableMap()

        Accounts
            .selectAll()
            .where {
                Accounts.isActive eq true
            }
            .collect { row ->
                val regionId =
                    row[Accounts.regionId]
                        ?.value

                if (
                    regionId != null &&
                    regionCounts.containsKey(
                        regionId
                    )
                ) {
                    val currentCount =
                        regionCounts[
                            regionId
                        ] ?: 0L

                    regionCounts[
                        regionId
                    ] = currentCount + 1L
                }
            }

        val statistics =
            regionRows.map { row ->
                val regionId =
                    row[Regions.id].value

                val regionName =
                    row[Regions.region]

                RegionStatResponse(
                    region =
                        regionName,

                    count =
                        regionCounts[
                            regionId
                        ] ?: 0L
                )
            }

        println(
            "Region statistics retrieved: $statistics"
        )

        return statistics
    }
}