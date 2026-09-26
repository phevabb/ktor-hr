package com.hr.admin.repositories

import com.hr.account.dtos.Gender
import com.hr.account.table.Accounts
import com.hr.admin.dtos.GenderStatResponse
import kotlinx.coroutines.flow.collect
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object GenderStatsRepository {

    suspend fun getAll():
            List<GenderStatResponse> {
        val genderValues =
            Gender.entries.toList()

        val genderCounts =
            genderValues
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
                val gender =
                    row[Accounts.gender]

                if (gender != null) {
                    val currentCount =
                        genderCounts[gender]
                            ?: 0L

                    genderCounts[gender] =
                        currentCount + 1L
                }
            }

        val statistics =
            genderValues.map { gender ->
                GenderStatResponse(
                    gender =
                        gender.name,

                    count =
                        genderCounts[gender]
                            ?: 0L
                )
            }

        println(
            "Gender statistics retrieved: $statistics"
        )

        return statistics
    }
}