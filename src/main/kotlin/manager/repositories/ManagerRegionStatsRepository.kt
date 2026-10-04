package com.hr.manager.repositories

import com.hr.account.table.Accounts
import com.hr.manager.dtos.ManagerRegionStatResponse
import com.hr.region.tables.Regions
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.fold
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagerRegionStatsRepository {

    suspend fun getRegionStats(
        regionId: Int
    ): List<ManagerRegionStatResponse> {
        println(
            "=================================================="
        )

        println(
            "Retrieving region statistics for Manager"
        )

        println(
            "Manager region ID: $regionId"
        )

        val regionRow =
            Regions
                .selectAll()
                .where {
                    Regions.id eq regionId
                }
                .firstOrNull()

        if (regionRow == null) {
            println(
                "Manager region record was not found"
            )

            println(
                "Missing region ID: $regionId"
            )

            println(
                "=================================================="
            )

            return emptyList()
        }

        val regionName =
            regionRow[
                Regions.region
            ]

        println(
            "Manager region name: $regionName"
        )

        val activeAccountCount =
            Accounts
                .selectAll()
                .where {
                    Accounts.isActive eq true
                }
                .fold(
                    initial = 0L
                ) {
                        count,
                        row ->

                    val accountRegionId =
                        row[
                            Accounts.regionId
                        ]
                            ?.value

                    if (
                        accountRegionId ==
                        regionId
                    ) {
                        count + 1L
                    } else {
                        count
                    }
                }

        println(
            "Active accounts in Manager region: $activeAccountCount"
        )

        val statistics =
            listOf(
                ManagerRegionStatResponse(
                    region =
                        regionName,

                    count =
                        activeAccountCount
                )
            )

        println(
            "Manager region statistics completed"
        )

        println(
            "Region: $regionName"
        )

        println(
            "Count: $activeAccountCount"
        )

        println(
            "=================================================="
        )

        return statistics
    }
}
