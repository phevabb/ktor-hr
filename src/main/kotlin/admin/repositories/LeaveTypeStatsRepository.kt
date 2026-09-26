package com.hr.admin.repositories

import com.hr.account.table.Accounts
import com.hr.admin.dtos.LeaveTypeStatResponse
import com.hr.onleavetype.table.OnLeaveTypes
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object LeaveTypeStatsRepository {

    suspend fun getAll():
            List<LeaveTypeStatResponse> {
        /*
         * Retrieve every leave type so that leave types
         * without assigned active accounts are returned
         * with a count of zero.
         */
        val leaveTypeRows =
            OnLeaveTypes
                .selectAll()
                .orderBy(
                    OnLeaveTypes.name,
                    SortOrder.ASC
                )
                .toList()

        val leaveTypeNamesById =
            leaveTypeRows.associate { row ->
                val leaveTypeId =
                    row[OnLeaveTypes.id].value

                val leaveTypeName =
                    row[OnLeaveTypes.name]

                leaveTypeId to
                        leaveTypeName
            }

        val leaveTypeCounts =
            leaveTypeNamesById.keys
                .associateWith {
                    0L
                }
                .toMutableMap()

        /*
         * Count only active accounts.
         *
         * Accounts without an assigned leave type
         * are not included in any leave-type count.
         */
        Accounts
            .selectAll()
            .where {
                Accounts.isActive eq true
            }
            .collect { row ->
                val leaveTypeId =
                    row[
                        Accounts.onLeaveTypeId
                    ]
                        ?.value

                if (
                    leaveTypeId != null &&
                    leaveTypeCounts
                        .containsKey(
                            leaveTypeId
                        )
                ) {
                    val currentCount =
                        leaveTypeCounts[
                            leaveTypeId
                        ] ?: 0L

                    leaveTypeCounts[
                        leaveTypeId
                    ] = currentCount + 1L
                }
            }

        val statistics =
            leaveTypeRows.map { row ->
                val leaveTypeId =
                    row[OnLeaveTypes.id]
                        .value

                val leaveTypeName =
                    row[OnLeaveTypes.name]

                LeaveTypeStatResponse(
                    leaveType =
                        leaveTypeName,

                    count =
                        leaveTypeCounts[
                            leaveTypeId
                        ] ?: 0L
                )
            }

        println(
            "Leave-type statistics retrieved: $statistics"
        )

        return statistics
    }
}