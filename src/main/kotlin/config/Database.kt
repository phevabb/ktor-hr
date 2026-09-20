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
import com.hr.onleavetype.table.OnLeaveTypes
import com.hr.position.table.Positions
import com.hr.region.tables.Regions
import com.hr.staffclass.table.StaffClasses
import com.hr.title.table.Titles
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.R2dbcTransaction
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils

import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

object DatabaseFactory {

    private lateinit var database: R2dbcDatabase

    fun init() {

        val databaseUrl =
            System.getenv("STOOLLANDS_DB_URL")
                ?: "r2dbc:postgresql://localhost:5432/ktor-stoollands"

        val databaseUser =
            System.getenv("STOOLLANDS_DB_USER")
                ?: "postgres"

        val databasePassword =
            System.getenv("STOOLLANDS_DB_PASSWORD")
                ?: "postgres"

        database = R2dbcDatabase.connect(
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
                    ChangeOfGrades,
                    AcademicQualifications,
                    Titles,
                    OnLeaveTypes,
                    StaffClasses,
                    Positions,
                    Accounts,
                    ManagerProfiles



                )

                SchemaUtils
                    .addMissingColumnsStatements(
                        Regions,
                        Districts,
                        Departments,
                        Classes,
                        ManagementUnits,
                        CurrentGrades,
                        ChangeOfGrades,
                        AcademicQualifications,
                        Titles,
                        OnLeaveTypes,
                        StaffClasses,
                        Positions,
                        Accounts,
                        ManagerProfiles
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
