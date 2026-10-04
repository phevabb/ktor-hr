package com.hr.manager.repositories

import com.hr.account.dtos.Professional
import com.hr.account.table.Accounts
import com.hr.manager.dtos.ManagerProfessionalStatResponse
import kotlinx.coroutines.flow.collect
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagerProfessionalStatsRepository {

    suspend fun getProfessionalStatsForRegion(
        regionId: Int
    ): List<ManagerProfessionalStatResponse> {
        println(
            "Retrieving professional statistics for Manager region"
        )

        println(
            "Manager region ID: $regionId"
        )

        val professionalTypes =
            Professional.entries
                .toList()

        val professionalCounts =
            professionalTypes
                .associateWith {
                    0L
                }
                .toMutableMap()

        var activeAccountsInRegion =
            0L

        var accountsWithProfessionalType =
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

                val professionalType =
                    row[Accounts.professional]

                if (professionalType != null) {
                    val currentCount =
                        professionalCounts[
                            professionalType
                        ] ?: 0L

                    professionalCounts[
                        professionalType
                    ] = currentCount + 1L

                    accountsWithProfessionalType +=
                        1L
                }
            }

        val statistics =
            professionalTypes.map {
                    professionalType ->

                ManagerProfessionalStatResponse(
                    professional =
                        professionalType.name,

                    count =
                        professionalCounts[
                            professionalType
                        ] ?: 0L
                )
            }

        println(
            "Manager professional statistics completed"
        )

        println(
            "Region ID: $regionId"
        )

        println(
            "Active accounts in region: $activeAccountsInRegion"
        )

        println(
            "Accounts with professional type: $accountsWithProfessionalType"
        )

        println(
            "Professional statistic records: ${statistics.size}"
        )

        statistics.forEach { statistic ->
            println(
                "Professional type: ${statistic.professional}, " +
                        "count=${statistic.count}"
            )
        }

        return statistics
    }
}