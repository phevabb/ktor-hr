package com.hr.admin.repositories

import com.hr.account.dtos.FulltimeContractStaff
import com.hr.account.table.Accounts
import com.hr.admin.dtos.ContractStatResponse
import kotlinx.coroutines.flow.collect
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ContractStatsRepository {

    suspend fun getAll():
            List<ContractStatResponse> {
        val contractValues =
            FulltimeContractStaff.entries
                .toList()

        val contractCounts =
            contractValues
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
                val contractType =
                    row[
                        Accounts.fulltimeContractStaff
                    ]

                if (contractType != null) {
                    val currentCount =
                        contractCounts[
                            contractType
                        ] ?: 0L

                    contractCounts[
                        contractType
                    ] = currentCount + 1L
                }
            }

        val statistics =
            contractValues.map {
                    contractType ->

                ContractStatResponse(
                    contractType =
                        contractType.name,

                    count =
                        contractCounts[
                            contractType
                        ] ?: 0L
                )
            }

        println(
            "Contract statistics retrieved: $statistics"
        )

        return statistics
    }
}