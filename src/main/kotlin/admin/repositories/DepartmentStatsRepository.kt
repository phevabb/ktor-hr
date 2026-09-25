package com.hr.admin.repositories

import com.hr.account.table.Accounts
import com.hr.admin.dtos.DepartmentStatResponse
import com.hr.department.table.Departments
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object DepartmentStatsRepository {

    suspend fun getAll():
            List<DepartmentStatResponse> {
        val departmentRows =
            Departments
                .selectAll()
                .orderBy(
                    Departments.departmentName,
                    SortOrder.ASC
                )
                .toList()

        val departmentNamesById =
            departmentRows.associate { row ->
                row[Departments.id].value to
                        row[Departments.departmentName]
            }

        val departmentCounts =
            departmentNamesById.keys
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
                val departmentId =
                    row[Accounts.directorateId]
                        ?.value

                if (
                    departmentId != null &&
                    departmentCounts.containsKey(
                        departmentId
                    )
                ) {
                    val currentCount =
                        departmentCounts[
                            departmentId
                        ] ?: 0L

                    departmentCounts[
                        departmentId
                    ] = currentCount + 1L
                }
            }

        val statistics =
            departmentRows.map { row ->
                val departmentId =
                    row[Departments.id].value

                val departmentName =
                    row[
                        Departments.departmentName
                    ]

                DepartmentStatResponse(
                    department =
                        departmentName,

                    count =
                        departmentCounts[
                            departmentId
                        ] ?: 0L
                )
            }

        println(
            "Department statistics retrieved: $statistics"
        )

        return statistics
    }
}