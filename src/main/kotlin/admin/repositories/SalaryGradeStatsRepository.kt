package com.hr.admin.repositories

import com.hr.account.dtos.SalaryLevel
import com.hr.account.table.Accounts
import com.hr.admin.dtos.SalaryGradeStatResponse
import kotlinx.coroutines.flow.collect
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object SalaryGradeStatsRepository {

    suspend fun getAll():
            List<SalaryGradeStatResponse> {
        val salaryLevels =
            SalaryLevel.entries
                .toList()
                .sortedBy { salaryLevel ->
                    extractLevelNumber(
                        salaryLevel
                    )
                }

        val salaryLevelCounts =
            salaryLevels
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
                }
            }

        val statistics =
            salaryLevels.map { salaryLevel ->
                SalaryGradeStatResponse(
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
            "Salary-grade statistics retrieved: $statistics"
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

    private fun extractLevelNumber(
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