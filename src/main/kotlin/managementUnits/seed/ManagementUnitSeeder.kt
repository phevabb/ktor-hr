package com.hr.managementUnits.seed

import com.hr.config.DatabaseFactory.dbQuery
import com.hr.managementUnits.tables.ManagementUnits
import org.jetbrains.exposed.v1.r2dbc.batchInsert

object ManagementUnitSeeder {

    suspend fun seed() {
        val managementUnits =
            try {
                readManagementUnitsFromResource()
            } catch (exception: Exception) {
                println(
                    "Management unit seeding failed: ${exception.message}"
                )

                return
            }

        if (managementUnits.isEmpty()) {
            println(
                "Management unit seeding skipped: " +
                        "management_unit_cost_centre.txt is empty"
            )

            return
        }

        try {
            val insertedRows =
                dbQuery {
                    ManagementUnits.batchInsert(
                        data = managementUnits,
                        ignore = true,
                        shouldReturnGeneratedValues = false
                    ) { managementUnitName ->
                        this[
                            ManagementUnits.managementUnitName
                        ] = managementUnitName
                    }
                }

            println(
                "Management unit seeding completed: " +
                        "${insertedRows.size} row(s) inserted"
            )
        } catch (exception: Exception) {
            println(
                "Management unit seeding failed: ${exception.message}"
            )

            exception.printStackTrace()
        }
    }

    private fun readManagementUnitsFromResource():
            List<String> {

        val resourcePath =
            "data/management_unit_cost_centre.txt"

        val inputStream =
            ManagementUnitSeeder::class.java
                .classLoader
                .getResourceAsStream(
                    resourcePath
                )
                ?: throw IllegalStateException(
                    "Resource file not found: $resourcePath"
                )

        return inputStream
            .bufferedReader(
                Charsets.UTF_8
            )
            .useLines { lines ->
                lines
                    .map { line ->
                        line.trim()
                    }
                    .filter { line ->
                        line.isNotBlank()
                    }
                    .filterNot { line ->
                        line.startsWith("#")
                    }
                    .filter { line ->
                        line.length <= 100
                    }
                    .distinctBy { line ->
                        line.lowercase()
                    }
                    .toList()
            }
    }
}