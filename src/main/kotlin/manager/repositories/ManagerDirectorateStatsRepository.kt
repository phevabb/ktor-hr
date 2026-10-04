package com.hr.manager.repositories

import com.hr.account.table.Accounts
import com.hr.department.table.Departments
import com.hr.manager.dtos.ManagerDirectorateStatResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagerDirectorateStatsRepository {

    suspend fun getDirectorateStatsForRegion(
        regionId: Int
    ): List<ManagerDirectorateStatResponse> {
        println(
            "=================================================="
        )

        println(
            "Retrieving directorate statistics for Manager region"
        )

        println(
            "Manager region ID: $regionId"
        )

        val departmentRows =
            Departments
                .selectAll()
                .orderBy(
                    Departments.departmentName,
                    SortOrder.ASC
                )
                .toList()

        println(
            "Total directorates retrieved: ${departmentRows.size}"
        )

        val directorateCounts =
            departmentRows
                .associate { row ->
                    row[
                        Departments.id
                    ].value to 0L
                }
                .toMutableMap()

        var activeAccountsInRegion =
            0L

        var accountsWithDirectorate =
            0L

        Accounts
            .selectAll()
            .where {
                Accounts.isActive eq true
            }
            .collect { row ->
                val accountRegionId =
                    row[
                        Accounts.regionId
                    ]
                        ?.value

                if (
                    accountRegionId !=
                    regionId
                ) {
                    return@collect
                }

                activeAccountsInRegion += 1L

                val directorateId =
                    row[
                        Accounts.directorateId
                    ]
                        ?.value

                if (
                    directorateId != null &&
                    directorateCounts
                        .containsKey(
                            directorateId
                        )
                ) {
                    val currentCount =
                        directorateCounts[
                            directorateId
                        ] ?: 0L

                    directorateCounts[
                        directorateId
                    ] = currentCount + 1L

                    accountsWithDirectorate +=
                        1L
                }
            }

        val statistics =
            departmentRows.map { row ->
                val directorateId =
                    row[
                        Departments.id
                    ].value

                val directorateName =
                    row[
                        Departments.departmentName
                    ]

                ManagerDirectorateStatResponse(
                    departmentName =
                        directorateName,

                    count =
                        directorateCounts[
                            directorateId
                        ] ?: 0L
                )
            }

        println(
            "Manager directorate statistics completed"
        )

        println(
            "Region ID: $regionId"
        )

        println(
            "Active accounts in region: $activeAccountsInRegion"
        )

        println(
            "Accounts with directorate: $accountsWithDirectorate"
        )

        println(
            "Directorate statistic records: ${statistics.size}"
        )

        statistics.forEach { statistic ->
            println(
                "Directorate: ${statistic.departmentName}, " +
                        "count=${statistic.count}"
            )
        }

        println(
            "=================================================="
        )

        return statistics
    }
}