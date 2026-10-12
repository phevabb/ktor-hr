package com.hr.currentgrade.seed

import com.hr.config.DatabaseFactory.dbQuery
import com.hr.currentgrade.tables.CurrentGrades

import org.jetbrains.exposed.v1.r2dbc.batchInsert

object CurrentGradeSeeder {

    suspend fun seed() {
        val currentGrades =
            try {
                readCurrentGradesFromResource()
            } catch (exception: Exception) {
                println(
                    "Current-grade seeding failed: ${exception.message}"
                )

                return
            }

        if (currentGrades.isEmpty()) {
            println(
                "Current-grade seeding skipped: current_grade.txt is empty"
            )

            return
        }

        try {
            val insertedRows =
                dbQuery {
                    CurrentGrades.batchInsert(
                        data = currentGrades,
                        ignore = true,
                        shouldReturnGeneratedValues = false
                    ) { currentGradeName ->
                        this[
                            CurrentGrades.currentGrade
                        ] = currentGradeName
                    }
                }

            println(
                "Current-grade seeding completed: " +
                        "${insertedRows.size} row(s) inserted"
            )
        } catch (exception: Exception) {
            println(
                "Current-grade seeding failed: ${exception.message}"
            )

            exception.printStackTrace()
        }
    }

    private fun readCurrentGradesFromResource():
            List<String> {

        val resourcePath =
            "data/current_grade.txt"

        val inputStream =
            CurrentGradeSeeder::class.java
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