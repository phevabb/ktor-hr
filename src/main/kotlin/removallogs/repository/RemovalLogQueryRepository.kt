package com.hr.removallogs.repository

import com.hr.removallogs.dto.RemovalLogResponse
import com.hr.removallogs.table.UserRemovalLogs
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.r2dbc.selectAll

object RemovalLogQueryRepository {

    suspend fun countAll(): Long {
        println(
            "Counting removal logs"
        )

        val total =
            UserRemovalLogs
                .selectAll()
                .count()

        println(
            "Removal-log count: $total"
        )

        return total
    }

    suspend fun getPage(
        page: Int,
        pageSize: Int
    ): List<RemovalLogResponse> {
        val normalizedPage =
            page.coerceAtLeast(
                1
            )

        val normalizedPageSize =
            pageSize.coerceIn(
                minimumValue = 1,
                maximumValue = 100
            )

        val calculatedOffset =
            (
                    normalizedPage -
                            1
                    ).toLong() *
                    normalizedPageSize.toLong()

        println(
            "Retrieving removal-log page"
        )

        println(
            "Page: $normalizedPage"
        )

        println(
            "Page size: $normalizedPageSize"
        )

        println(
            "Offset: $calculatedOffset"
        )

        val rows =
            UserRemovalLogs
                .selectAll()
                .orderBy(
                    UserRemovalLogs.removedAt,
                    SortOrder.DESC
                )
                .orderBy(
                    UserRemovalLogs.id,
                    SortOrder.DESC
                )
                .limit(
                    normalizedPageSize
                )
                .offset(
                    calculatedOffset
                )
                .toList()

        println(
            "Removal-log rows retrieved: ${rows.size}"
        )

        return rows.map { row ->
            RemovalLogResponse(
                id =
                    row[
                        UserRemovalLogs.id
                    ].value,

                accountId =
                    row[
                        UserRemovalLogs.accountId
                    ].value,

                removedByAccountId =
                    row[
                        UserRemovalLogs.removedByAccountId
                    ].value,

                removedByRole =
                    row[
                        UserRemovalLogs.removedByRole
                    ],

                reason =
                    row[
                        UserRemovalLogs.reason
                    ],

                removedAt =
                    row[
                        UserRemovalLogs.removedAt
                    ].toString()
            )
        }
    }
}