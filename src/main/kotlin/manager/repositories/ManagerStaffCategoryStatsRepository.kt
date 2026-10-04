package com.hr.manager.repositories

import com.hr.account.dtos.StaffCategory
import com.hr.account.table.Accounts
import com.hr.manager.dtos.ManagerStaffCategoryStatResponse
import kotlinx.coroutines.flow.collect
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagerStaffCategoryStatsRepository {

    suspend fun getStaffCategoryStatsForRegion(
        regionId: Int
    ): List<ManagerStaffCategoryStatResponse> {
        println(
            "=================================================="
        )

        println(
            "Retrieving staff-category statistics for Manager region"
        )

        println(
            "Manager region ID: $regionId"
        )

        val staffCategories =
            StaffCategory.entries
                .toList()

        val staffCategoryCounts =
            staffCategories
                .associateWith {
                    0L
                }
                .toMutableMap()

        var activeAccountsInRegion =
            0L

        var accountsWithStaffCategory =
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

                val staffCategory =
                    row[
                        Accounts.staffCategory
                    ]

                if (staffCategory != null) {
                    val currentCount =
                        staffCategoryCounts[
                            staffCategory
                        ] ?: 0L

                    staffCategoryCounts[
                        staffCategory
                    ] = currentCount + 1L

                    accountsWithStaffCategory +=
                        1L
                }
            }

        val statistics =
            staffCategories.map { staffCategory ->
                ManagerStaffCategoryStatResponse(
                    staffCategory =
                        formatStaffCategory(
                            staffCategory
                        ),

                    count =
                        staffCategoryCounts[
                            staffCategory
                        ] ?: 0L
                )
            }

        println(
            "Manager staff-category statistics completed"
        )

        println(
            "Region ID: $regionId"
        )

        println(
            "Active accounts in region: $activeAccountsInRegion"
        )

        println(
            "Accounts with staff category: $accountsWithStaffCategory"
        )

        println(
            "Staff-category records: ${statistics.size}"
        )

        statistics.forEach { statistic ->
            println(
                "Staff category: ${statistic.staffCategory}, " +
                        "count=${statistic.count}"
            )
        }

        println(
            "=================================================="
        )

        return statistics
    }

    private fun formatStaffCategory(
        staffCategory: StaffCategory
    ): String {
        return staffCategory.name
            .replace(
                oldChar = '_',
                newChar = ' '
            )
    }
}