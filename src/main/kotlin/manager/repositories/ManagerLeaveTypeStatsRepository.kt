package com.hr.manager.repositories

import com.hr.account.table.Accounts
import com.hr.manager.dtos.ManagerLeaveTypeStatResponse
import com.hr.onleavetype.table.OnLeaveTypes
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagerLeaveTypeStatsRepository {

    suspend fun getLeaveTypeStatsForRegion(
        regionId: Int
    ): List<ManagerLeaveTypeStatResponse> {
        println(
            "=================================================="
        )

        println(
            "Retrieving leave-type statistics for Manager region"
        )

        println(
            "Manager region ID: $regionId"
        )

        val leaveTypeRows =
            OnLeaveTypes
                .selectAll()
                .orderBy(
                    OnLeaveTypes.name,
                    SortOrder.ASC
                )
                .toList()

        println(
            "Total leave types retrieved: ${leaveTypeRows.size}"
        )

        val leaveTypeCounts =
            leaveTypeRows
                .associate { row ->
                    row[
                        OnLeaveTypes.id
                    ].value to 0L
                }
                .toMutableMap()

        var activeAccountsInRegion =
            0L

        var accountsWithLeaveType =
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

                activeAccountsInRegion +=
                    1L

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

                    accountsWithLeaveType +=
                        1L
                }
            }

        val statistics =
            leaveTypeRows.map { row ->
                val leaveTypeId =
                    row[
                        OnLeaveTypes.id
                    ].value

                val leaveTypeName =
                    row[
                        OnLeaveTypes.name
                    ]

                ManagerLeaveTypeStatResponse(
                    leaveType =
                        leaveTypeName,

                    count =
                        leaveTypeCounts[
                            leaveTypeId
                        ] ?: 0L
                )
            }

        println(
            "Manager leave-type statistics completed"
        )

        println(
            "Region ID: $regionId"
        )

        println(
            "Active accounts in region: $activeAccountsInRegion"
        )

        println(
            "Accounts with leave type: $accountsWithLeaveType"
        )

        println(
            "Leave-type statistic records: ${statistics.size}"
        )

        statistics.forEach { statistic ->
            println(
                "Leave type: ${statistic.leaveType}, " +
                        "count=${statistic.count}"
            )
        }

        println(
            "=================================================="
        )

        return statistics
    }
}