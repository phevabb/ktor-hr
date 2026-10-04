package com.hr.manager.repositories

import com.hr.account.table.Accounts
import com.hr.classes.tables.Classes
import com.hr.manager.dtos.ManagerClassStatResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagerClassStatsRepository {

    suspend fun getClassStatsForRegion(
        regionId: Int
    ): List<ManagerClassStatResponse> {
        println(
            "Retrieving class statistics for Manager region"
        )

        println(
            "Manager region ID: $regionId"
        )

        val classRows =
            Classes
                .selectAll()
                .orderBy(
                    Classes.classesName,
                    SortOrder.ASC
                )
                .toList()

        println(
            "Total classes found: ${classRows.size}"
        )

        val classCounts =
            classRows
                .associate { row ->
                    row[Classes.id].value to 0L
                }
                .toMutableMap()

        var activeAccountsInRegion =
            0L

        var accountsWithClass =
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
                    accountRegionId != regionId
                ) {
                    return@collect
                }

                activeAccountsInRegion += 1L

                val classId =
                    row[Accounts.categoryId]
                        ?.value

                if (
                    classId != null &&
                    classCounts.containsKey(
                        classId
                    )
                ) {
                    classCounts[classId] =
                        (
                                classCounts[classId]
                                    ?: 0L
                                ) + 1L

                    accountsWithClass += 1L
                }
            }

        val statistics =
            classRows.map { row ->
                val classId =
                    row[Classes.id].value

                val className =
                    row[Classes.classesName]

                ManagerClassStatResponse(
                    className =
                        className,

                    count =
                        classCounts[classId]
                            ?: 0L
                )
            }

        println(
            "Manager class statistics completed"
        )

        println(
            "Region ID: $regionId"
        )

        println(
            "Active accounts in region: $activeAccountsInRegion"
        )

        println(
            "Active accounts with a class: $accountsWithClass"
        )

        println(
            "Class statistic records: ${statistics.size}"
        )

        println(
            "Class statistics: $statistics"
        )

        return statistics
    }
}