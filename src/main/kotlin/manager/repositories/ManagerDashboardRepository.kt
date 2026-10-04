package com.hr.manager.repositories

import com.hr.account.dtos.Gender
import com.hr.account.dtos.Role
import com.hr.account.table.Accounts
import com.hr.manager.dtos.ManagerDashboardSummaryResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagerDashboardRepository {

    suspend fun findManagerAccount(
        accountId: Int
    ): ResultRow? {
        println(
            "Finding Manager account: accountId=$accountId"
        )

        return Accounts
            .selectAll()
            .where {
                Accounts.id eq accountId
            }
            .firstOrNull()
    }

    fun isActive(
        row: ResultRow
    ): Boolean {
        val active =
            row[Accounts.isActive]

        println(
            "Manager account active status: $active"
        )

        return active
    }

    fun isManager(
        row: ResultRow
    ): Boolean {
        val role =
            row[Accounts.role]

        println(
            "Manager account database role: $role"
        )

        return role ==
                Role.Manager
    }

    fun getRegionId(
        row: ResultRow
    ): Int? {
        val regionId =
            row[Accounts.regionId]
                ?.value

        println(
            "Manager assigned region ID: $regionId"
        )

        return regionId
    }

    suspend fun getSummaryForRegion(
        regionId: Int
    ): ManagerDashboardSummaryResponse {
        println(
            "Calculating dashboard summary for Manager region"
        )

        println(
            "Manager region ID: $regionId"
        )

        var numberOfUsers =
            0L

        var numberOfFemales =
            0L

        var numberOfMales =
            0L

        var numberOfStaff =
            0L

        var numberOfAdmins =
            0L

        var numberOfManagers =
            0L

        Accounts
            .selectAll()
            .where {
                (
                        Accounts.isActive eq true
                        ) and (
                        Accounts.regionId eq
                                regionId
                        )
            }
            .collect { row ->
                numberOfUsers += 1L

                when (
                    row[Accounts.gender]
                ) {
                    Gender.Female -> {
                        numberOfFemales += 1L
                    }

                    Gender.Male -> {
                        numberOfMales += 1L
                    }

                    null -> {
                        println(
                            "Active account has no gender: " +
                                    "accountId=${row[Accounts.id].value}"
                        )
                    }
                }

                when (
                    row[Accounts.role]
                ) {
                    Role.Staff -> {
                        numberOfStaff += 1L
                    }

                    Role.Admin -> {
                        numberOfAdmins += 1L
                    }

                    Role.Manager -> {
                        numberOfManagers += 1L
                    }

                    null -> {
                        println(
                            "Active account has no role: " +
                                    "accountId=${row[Accounts.id].value}"
                        )
                    }
                }
            }

        val summary =
            ManagerDashboardSummaryResponse(
                numOfUsers =
                    numberOfUsers,

                numOfFemales =
                    numberOfFemales,

                numOfMales =
                    numberOfMales,

                numOfStaffs =
                    numberOfStaff,

                numOfAdmins =
                    numberOfAdmins,

                numOfManagers =
                    numberOfManagers
            )

        println(
            "Manager dashboard summary generated"
        )

        println(
            "Region ID: $regionId"
        )

        println(
            "Active users: ${summary.numOfUsers}"
        )

        println(
            "Females: ${summary.numOfFemales}"
        )

        println(
            "Males: ${summary.numOfMales}"
        )

        println(
            "Staff: ${summary.numOfStaffs}"
        )

        println(
            "Admins: ${summary.numOfAdmins}"
        )

        println(
            "Managers: ${summary.numOfManagers}"
        )

        return summary
    }
}