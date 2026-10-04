package com.hr.manager.repositories

import com.hr.account.dtos.SalaryLevel
import com.hr.account.table.Accounts
import com.hr.manager.dtos.ManagerSalaryGradeStatResponse
import kotlinx.coroutines.flow.collect
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagerSalaryGradeStatsRepository {

    suspend fun getSalaryGradeStatsForRegion(
        regionId: Int
    ): List<ManagerSalaryGradeStatResponse> {
        println(
            "=================================================="
        )

        println(
            "Retrieving salary-grade statistics for Manager region"
        )

        println(
            "Manager region ID: $regionId"
        )

        val salaryLevels =
            SalaryLevel.entries
                .sortedBy { salaryLevel ->
                    getSalaryLevelNumber(
                        salaryLevel
                    )
                }

        val salaryLevelCounts =
            salaryLevels
                .associateWith {
                    0L
                }
                .toMutableMap()

        var activeAccountsInRegion =
            0L

        var accountsWithSalaryLevel =
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

                val salaryLevel =
                    row[
                        Accounts.currentSalaryLevel
                    ]

                if (salaryLevel != null) {
                    val currentCount =
                        salaryLevelCounts[
                            salaryLevel
                        ] ?: 0L

                    salaryLevelCounts[
                        salaryLevel
                    ] = currentCount + 1L

                    accountsWithSalaryLevel += 1L
                }
            }

        val statistics =
            salaryLevels.map { salaryLevel ->
                ManagerSalaryGradeStatResponse(
                    salaryRange =
                        formatSalaryLevel(
                            salaryLevel
                        ),

                    count =
                        salaryLevelCounts[
                            salaryLevel
                        ] ?: 0L
                )
            }

        println(
            "Manager salary-grade statistics completed"
        )

        println(
            "Region ID: $regionId"
        )

        println(
            "Active accounts in region: $activeAccountsInRegion"
        )

        println(
            "Accounts with salary level: $accountsWithSalaryLevel"
        )

        println(
            "Salary-grade statistic records: ${statistics.size}"
        )

        statistics.forEach { statistic ->
            println(
                "Salary range: ${statistic.salaryRange}, " +
                        "count=${statistic.count}"
            )
        }

        println(
            "=================================================="
        )

        return statistics
    }

    private fun formatSalaryLevel(
        salaryLevel: SalaryLevel
    ): String {
        return salaryLevel.name
            .replace(
                oldValue = "SS_",
                newValue = "SS."
            )
    }

    private fun getSalaryLevelNumber(
        salaryLevel: SalaryLevel
    ): Int {
        return salaryLevel.name
            .substringAfter(
                delimiter = "SS_"
            )
            .toIntOrNull()
            ?: Int.MAX_VALUE
    }
}