package com.hr.admin.repositories

import com.hr.account.table.Accounts
import com.hr.admin.dtos.AgeStatResponse
import kotlinx.coroutines.flow.collect
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll
import java.time.LocalDate
import java.time.Period

object AgeStatsRepository {

    suspend fun getAll():
            List<AgeStatResponse> {
        val ageRanges =
            linkedMapOf(
                "20 - 30" to
                        IntRange(
                            start = 20,
                            endInclusive = 30
                        ),

                "31 - 40" to
                        IntRange(
                            start = 31,
                            endInclusive = 40
                        ),

                "41 - 50" to
                        IntRange(
                            start = 41,
                            endInclusive = 50
                        ),

                "51 - 60" to
                        IntRange(
                            start = 51,
                            endInclusive = 60
                        ),

                "61+" to
                        IntRange(
                            start = 61,
                            endInclusive = 100
                        )
            )

        val ageRangeCounts =
            ageRanges.keys
                .associateWith {
                    0L
                }
                .toMutableMap()

        val currentDate =
            LocalDate.now()

        Accounts
            .selectAll()
            .where {
                Accounts.isActive eq true
            }
            .collect { row ->
                val dateOfBirth =
                    row[Accounts.dateOfBirth]

                if (
                    dateOfBirth != null &&
                    !dateOfBirth.isAfter(
                        currentDate
                    )
                ) {
                    val age =
                        calculateAge(
                            dateOfBirth =
                                dateOfBirth,

                            currentDate =
                                currentDate
                        )

                    val matchingRange =
                        ageRanges.entries
                            .firstOrNull {
                                    ageRange ->

                                age in
                                        ageRange.value
                            }

                    if (matchingRange != null) {
                        val rangeLabel =
                            matchingRange.key

                        val currentCount =
                            ageRangeCounts[
                                rangeLabel
                            ] ?: 0L

                        ageRangeCounts[
                            rangeLabel
                        ] = currentCount + 1L
                    }
                }
            }

        val statistics =
            ageRanges.keys.map {
                    rangeLabel ->

                AgeStatResponse(
                    ageRange =
                        rangeLabel,

                    count =
                        ageRangeCounts[
                            rangeLabel
                        ] ?: 0L
                )
            }

        println(
            "Age statistics retrieved: $statistics"
        )

        return statistics
    }

    private fun calculateAge(
        dateOfBirth: LocalDate,
        currentDate: LocalDate
    ): Int {
        return Period.between(
            dateOfBirth,
            currentDate
        ).years
    }
}