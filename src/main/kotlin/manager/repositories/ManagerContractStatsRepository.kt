package com.hr.manager.repositories

import com.hr.account.dtos.FulltimeContractStaff
import com.hr.account.table.Accounts
import com.hr.manager.dtos.ManagerContractStatResponse
import kotlinx.coroutines.flow.collect
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagerContractStatsRepository {

    suspend fun getContractStatsForRegion(
        regionId: Int
    ): List<ManagerContractStatResponse> {
        println(
            "Retrieving contract statistics for Manager region"
        )

        println(
            "Manager region ID: $regionId"
        )

        val contractTypes =
            FulltimeContractStaff
                .entries
                .toList()

        val contractCounts =
            contractTypes
                .associateWith {
                    0L
                }
                .toMutableMap()

        var activeAccountsInRegion =
            0L

        var accountsWithContractType =
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

                activeAccountsInRegion +=
                    1L

                val contractType =
                    row[
                        Accounts
                            .fulltimeContractStaff
                    ]

                if (contractType != null) {
                    val currentCount =
                        contractCounts[
                            contractType
                        ] ?: 0L

                    contractCounts[
                        contractType
                    ] = currentCount + 1L

                    accountsWithContractType +=
                        1L
                }
            }

        val statistics =
            contractTypes.map {
                    contractType ->

                ManagerContractStatResponse(
                    contractType =
                        contractType.name,

                    count =
                        contractCounts[
                            contractType
                        ] ?: 0L
                )
            }

        println(
            "Manager contract statistics completed"
        )

        println(
            "Region ID: $regionId"
        )

        println(
            "Active accounts in region: $activeAccountsInRegion"
        )

        println(
            "Accounts with contract type: $accountsWithContractType"
        )

        println(
            "Contract statistic records: ${statistics.size}"
        )

        statistics.forEach { statistic ->
            println(
                "Contract type: ${statistic.contractType}, " +
                        "count=${statistic.count}"
            )
        }

        return statistics
    }
}