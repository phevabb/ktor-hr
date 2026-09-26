package com.hr.admin.repositories

import com.hr.account.dtos.StaffCategory
import com.hr.account.table.Accounts
import com.hr.admin.dtos.StaffCategoryStatResponse
import kotlinx.coroutines.flow.collect
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object StaffCategoryStatsRepository {

    suspend fun getAll():
            List<StaffCategoryStatResponse> {
        val staffCategoryValues =
            StaffCategory.entries
                .toList()

        val staffCategoryCounts =
            staffCategoryValues
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
                }
            }

        val statistics =
            staffCategoryValues.map {
                    staffCategory ->

                StaffCategoryStatResponse(
                    staffCategory =
                        staffCategory.name,

                    count =
                        staffCategoryCounts[
                            staffCategory
                        ] ?: 0L
                )
            }

        println(
            "Staff category statistics retrieved: $statistics"
        )

        return statistics
    }
}
