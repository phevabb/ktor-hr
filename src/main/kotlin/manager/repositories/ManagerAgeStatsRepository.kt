package com.hr.manager.repositories

import com.hr.account.table.Accounts
import com.hr.manager.dtos.ManagerAgeStatResponse
import java.time.LocalDate
import java.time.Period
import kotlinx.coroutines.flow.collect
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagerAgeStatsRepository {

    private data class AgeRange(
        val label: String,
        val minimumAge: Int,
        val maximumAge: Int?
    )

    private val ageRanges =
        listOf(
            AgeRange(
                label = "20 - 30",
                minimumAge = 20,
                maximumAge = 30
            ),

            AgeRange(
                label = "31 - 40",
                minimumAge = 31,
                maximumAge = 40
            ),

            AgeRange(
                label = "41 - 50",
                minimumAge = 41,
                maximumAge = 50
            ),

            AgeRange(
                label = "51 - 60",
                minimumAge = 51,
                maximumAge = 60
            ),

            AgeRange(
                label = "61+",
                minimumAge = 61,
                maximumAge = null
            )
        )

    suspend fun getAgeStatsForRegion(
        regionId: Int
    ): List<ManagerAgeStatResponse> {
        println(
            "=================================================="
        )

        println(
            "Retrieving age statistics for Manager region"
        )

        println(
            "Manager region ID: $regionId"
        )

        val ageCounts =
            ageRanges
                .associate { ageRange ->
                    ageRange.label to 0L
                }
                .toMutableMap()

        val today =
            LocalDate.now()

        var activeAccountsInRegion =
            0L

        var accountsWithDateOfBirth =
            0L

        var accountsWithValidAge =
            0L

        var accountsOutsideConfiguredRanges =
            0L

        Accounts
            .selectAll()
            .where {
                Accounts.isActive eq true
            }
            .collect { row ->
                val accountRegionId =
                    row[Accounts.regionId]
                        ?.value

                if (
                    accountRegionId != regionId
                ) {
                    return@collect
                }

                activeAccountsInRegion += 1L

                val dateOfBirth =
                    row[Accounts.dateOfBirth]
                        ?: return@collect

                accountsWithDateOfBirth += 1L

                val age =
                    calculateAge(
                        dateOfBirth = dateOfBirth,
                        currentDate = today
                    )
                        ?: return@collect

                accountsWithValidAge += 1L

                val matchedRange =
                    ageRanges.firstOrNull { ageRange ->
                        isAgeInRange(
                            age = age,
                            ageRange = ageRange
                        )
                    }

                if (matchedRange == null) {
                    accountsOutsideConfiguredRanges +=
                        1L

                    println(
                        "Account age is outside configured ranges"
                    )

                    println(
                        "Account ID: ${row[Accounts.id].value}"
                    )

                    println(
                        "Calculated age: $age"
                    )

                    return@collect
                }

                ageCounts[matchedRange.label] =
                    (
                            ageCounts[
                                matchedRange.label
                            ] ?: 0L
                            ) + 1L
            }

        val statistics =
            ageRanges.map { ageRange ->
                ManagerAgeStatResponse(
                    ageRange =
                        ageRange.label,

                    count =
                        ageCounts[
                            ageRange.label
                        ] ?: 0L
                )
            }

        println(
            "Manager age statistics completed"
        )

        println(
            "Region ID: $regionId"
        )

        println(
            "Calculation date: $today"
        )

        println(
            "Active accounts in region: $activeAccountsInRegion"
        )

        println(
            "Accounts with date of birth: $accountsWithDateOfBirth"
        )

        println(
            "Accounts with valid age: $accountsWithValidAge"
        )

        println(
            "Accounts outside configured ranges: $accountsOutsideConfiguredRanges"
        )

        println(
            "Age statistic records: ${statistics.size}"
        )

        statistics.forEach { statistic ->
            println(
                "Age range: ${statistic.ageRange}, " +
                        "count=${statistic.count}"
            )
        }

        println(
            "=================================================="
        )

        return statistics
    }

    private fun calculateAge(
        dateOfBirth: LocalDate,
        currentDate: LocalDate
    ): Int? {
        if (
            dateOfBirth.isAfter(
                currentDate
            )
        ) {
            println(
                "Invalid future date of birth ignored: $dateOfBirth"
            )

            return null
        }

        return Period
            .between(
                dateOfBirth,
                currentDate
            )
            .years
    }

    private fun isAgeInRange(
        age: Int,
        ageRange: AgeRange
    ): Boolean {
        if (
            age <
            ageRange.minimumAge
        ) {
            return false
        }

        val maximumAge =
            ageRange.maximumAge

        return (
                maximumAge == null ||
                        age <= maximumAge
                )
    }
}