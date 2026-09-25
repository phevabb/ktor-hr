package com.hr.admin.repositories

import com.hr.account.dtos.Gender
import com.hr.account.dtos.Role
import com.hr.account.table.Accounts
import com.hr.admin.dtos.AdminDashboardSummaryResponse
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object AdminDashboardRepository {

    suspend fun getDashboardSummary():
            AdminDashboardSummaryResponse {
        var numOfUsers =
            0L

        var numOfFemales =
            0L

        var numOfMales =
            0L

        var numOfStaffs =
            0L

        var numOfAdmins =
            0L

        var numOfManagers =
            0L

        Accounts
            .selectAll()
            .where {
                Accounts.isActive eq true
            }
            .collect { row ->
                numOfUsers += 1L

                when (
                    row[Accounts.gender]
                ) {
                    Gender.Female -> {
                        numOfFemales += 1L
                    }

                    Gender.Male -> {
                        numOfMales += 1L
                    }

                    null -> {
                        /*
                         * No gender count is incremented.
                         */
                    }
                }

                when (
                    row[Accounts.role]
                ) {
                    Role.Staff -> {
                        numOfStaffs += 1L
                    }

                    Role.Admin -> {
                        numOfAdmins += 1L
                    }

                    Role.Manager -> {
                        numOfManagers += 1L
                    }

                    null -> {
                        /*
                         * No role count is incremented.
                         */
                    }
                }
            }

        val summary =
            AdminDashboardSummaryResponse(
                numOfUsers =
                    numOfUsers,

                numOfFemales =
                    numOfFemales,

                numOfMales =
                    numOfMales,

                numOfStaffs =
                    numOfStaffs,

                numOfAdmins =
                    numOfAdmins,

                numOfManagers =
                    numOfManagers
            )

        println(
            """
            Admin dashboard summary:
            Active users: ${summary.numOfUsers}
            Female users: ${summary.numOfFemales}
            Male users: ${summary.numOfMales}
            Staff users: ${summary.numOfStaffs}
            Admin users: ${summary.numOfAdmins}
            Manager users: ${summary.numOfManagers}
            """.trimIndent()
        )

        return summary
    }
}