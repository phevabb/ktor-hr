package com.hr.manager.repositories

import com.hr.account.dtos.Gender
import com.hr.account.table.Accounts
import com.hr.manager.dtos.ManagerGenderStatResponse
import kotlinx.coroutines.flow.collect
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagerGenderStatsRepository {

    suspend fun getGenderStatsForRegion(
        regionId: Int
    ): List<ManagerGenderStatResponse> {
        println(
            "=================================================="
        )

        println(
            "Retrieving gender statistics for Manager region"
        )

        println(
            "Manager region ID: $regionId"
        )

        val genders =
            Gender.entries.toList()

        val genderCounts =
            genders
                .associateWith {
                    0L
                }
                .toMutableMap()

        var activeAccountsInRegion =
            0L

        var accountsWithGender =
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
                    accountRegionId !=
                    regionId
                ) {
                    return@collect
                }

                activeAccountsInRegion +=
                    1L

                val gender =
                    row[Accounts.gender]

                if (gender != null) {
                    val currentCount =
                        genderCounts[
                            gender
                        ] ?: 0L

                    genderCounts[
                        gender
                    ] = currentCount + 1L

                    accountsWithGender +=
                        1L
                }
            }

        val statistics =
            genders.map { gender ->
                ManagerGenderStatResponse(
                    gender =
                        gender.name,

                    count =
                        genderCounts[
                            gender
                        ] ?: 0L
                )
            }

        println(
            "Manager gender statistics completed"
        )

        println(
            "Region ID: $regionId"
        )

        println(
            "Active accounts in region: $activeAccountsInRegion"
        )

        println(
            "Accounts with gender: $accountsWithGender"
        )

        println(
            "Gender statistic records: ${statistics.size}"
        )

        statistics.forEach { statistic ->
            println(
                "Gender: ${statistic.gender}, " +
                        "count=${statistic.count}"
            )
        }

        println(
            "=================================================="
        )

        return statistics
    }
}