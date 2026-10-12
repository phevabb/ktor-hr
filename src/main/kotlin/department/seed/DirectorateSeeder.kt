package com.hr.department.seed

import com.hr.config.DatabaseFactory.dbQuery
import com.hr.department.table.Departments
import org.jetbrains.exposed.v1.r2dbc.batchInsert

object DirectorateSeeder {

    suspend fun seed() {
        val directorates =
            try {
                readDirectoratesFromResource()
            } catch (exception: Exception) {
                println(
                    "Directorate seeding failed: ${exception.message}"
                )

                return
            }

        if (directorates.isEmpty()) {
            println(
                "Directorate seeding skipped: directorate.txt is empty"
            )

            return
        }

        try {
            val insertedRows =
                dbQuery {
                    Departments.batchInsert(
                        data = directorates,
                        ignore = true,
                        shouldReturnGeneratedValues = false
                    ) { directorateName ->
                        this[
                            Departments.departmentName
                        ] = directorateName
                    }
                }

            println(
                "Directorate seeding completed: " +
                        "${insertedRows.size} row(s) inserted"
            )
        } catch (exception: Exception) {
            println(
                "Directorate seeding failed: ${exception.message}"
            )

            exception.printStackTrace()
        }
    }

    private fun readDirectoratesFromResource():
            List<String> {

        val resourcePath =
            "data/directorate.txt"

        val inputStream =
            DirectorateSeeder::class.java
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
                        line.length <= 120
                    }
                    .distinctBy { line ->
                        line.lowercase()
                    }
                    .toList()
            }
    }
}