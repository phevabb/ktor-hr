package com.hr.config

import com.hr.academicqualification.table.AcademicQualifications
import com.hr.account.table.Accounts
import com.hr.changeofgrade.tables.ChangeOfGrades
import com.hr.classes.tables.Classes
import com.hr.currentgrade.tables.CurrentGrades
import com.hr.department.table.Departments
import com.hr.districts.tables.Districts
import com.hr.managementUnits.tables.ManagementUnits
import com.hr.managerprofile.table.ManagerProfiles
import com.hr.nextgrade.tables.NextGrades
import com.hr.onleavetype.table.OnLeaveTypes
import com.hr.password.table.PasswordResetTokens
import com.hr.position.table.Positions
import com.hr.region.tables.Regions
import com.hr.removallogs.table.UserRemovalLogs
import com.hr.staffclass.table.StaffClasses
import com.hr.title.table.Titles
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.R2dbcTransaction
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

object DatabaseFactory {

    private lateinit var database: R2dbcDatabase

    private val environment =
        dotenv {
            ignoreIfMissing = true
        }

    private fun requiredEnvironmentValue(
        name: String
    ): String {
        return System.getenv(name)
            ?.trim()
            ?.takeIf {
                it.isNotBlank()
            }
            ?: environment[name]
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }
            ?: throw IllegalStateException(
                "Missing required environment variable: $name"
            )
    }

    fun init() {
        val databaseUrl =
            requiredEnvironmentValue(
                "STOOLLANDS_DB_URL"
            )

        val databaseUser =
            requiredEnvironmentValue(
                "STOOLLANDS_DB_USER"
            )

        val databasePassword =
            requiredEnvironmentValue(
                "STOOLLANDS_DB_PASSWORD"
            )

        database =
            R2dbcDatabase.connect(
                url = databaseUrl,
                user = databaseUser,
                password = databasePassword
            )

        runBlocking {
            suspendTransaction(
                db = database
            ) {
                SchemaUtils.create(
                    Regions,
                    Districts,
                    Departments,
                    Classes,
                    ManagementUnits,
                    CurrentGrades,
                    NextGrades,
                    ChangeOfGrades,
                    AcademicQualifications,
                    Titles,
                    OnLeaveTypes,
                    StaffClasses,
                    Positions,
                    Accounts,
                    ManagerProfiles,
                    UserRemovalLogs,
                    PasswordResetTokens
                )

                SchemaUtils
                    .addMissingColumnsStatements(
                        Regions,
                        Districts,
                        Departments,
                        Classes,
                        ManagementUnits,
                        CurrentGrades,
                        NextGrades,
                        ChangeOfGrades,
                        AcademicQualifications,
                        Titles,
                        OnLeaveTypes,
                        StaffClasses,
                        Positions,
                        Accounts,
                        ManagerProfiles,
                        UserRemovalLogs,
                        PasswordResetTokens
                    )
                    .forEach { statement ->
                        exec(statement)
                    }
            }
        }
    }

    suspend fun <T> dbQuery(
        block: suspend R2dbcTransaction.() -> T
    ): T {
        return suspendTransaction(
            db = database
        ) {
            block()
        }
    }
}