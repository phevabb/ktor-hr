package com.hr.admin.repositories

import com.hr.account.table.Accounts
import com.hr.admin.dtos.ClassStatResponse
import com.hr.classes.tables.Classes
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ClassStatsRepository {

    suspend fun getAll():
            List<ClassStatResponse> {
        val classRows =
            Classes
                .selectAll()
                .orderBy(
                    Classes.classesName,
                    SortOrder.ASC
                )
                .toList()

        val classCounts =
            classRows
                .associate { row ->
                    row[Classes.id].value to 0L
                }
                .toMutableMap()

        Accounts
            .selectAll()
            .where {
                Accounts.isActive eq true
            }
            .collect { row ->
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
                }
            }

        val statistics =
            classRows.map { row ->
                val classId =
                    row[Classes.id].value

                val className =
                    row[
                        Classes.classesName
                    ]

                ClassStatResponse(
                    className =
                        className,

                    count =
                        classCounts[classId]
                            ?: 0L
                )
            }

        println(
            "Class statistics retrieved: $statistics"
        )

        return statistics
    }
}