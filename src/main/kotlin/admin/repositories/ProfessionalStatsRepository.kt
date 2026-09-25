package com.hr.admin.repositories

import com.hr.account.dtos.Professional
import com.hr.account.table.Accounts
import com.hr.admin.dtos.ProfessionalStatResponse
import kotlinx.coroutines.flow.collect
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ProfessionalStatsRepository {

    suspend fun getAll():
            List<ProfessionalStatResponse> {
        val professionalValues =
            Professional.entries
                .toList()

        val professionalCounts =
            professionalValues
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
                val professional =
                    row[
                        Accounts.professional
                    ]

                if (professional != null) {
                    val currentCount =
                        professionalCounts[
                            professional
                        ] ?: 0L

                    professionalCounts[
                        professional
                    ] = currentCount + 1L
                }
            }

        val statistics =
            professionalValues.map {
                    professional ->

                ProfessionalStatResponse(
                    professional =
                        professional.name,

                    count =
                        professionalCounts[
                            professional
                        ] ?: 0L
                )
            }

        println(
            "Professional statistics retrieved: $statistics"
        )

        return statistics
    }
}