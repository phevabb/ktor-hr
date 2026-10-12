package com.hr.changeofgrade.seed

import com.hr.changeofgrade.tables.ChangeOfGrades
import com.hr.config.DatabaseFactory.dbQuery
import org.jetbrains.exposed.v1.r2dbc.batchInsert

object ChangeOfGradeSeeder {

    suspend fun seed() {
        val gradeChanges =
            try {
                readGradeChangesFromResource()
            } catch (exception: Exception) {
                println(
                    "Change-of-grade seeding failed: ${exception.message}"
                )

                return
            }

        if (gradeChanges.isEmpty()) {
            println(
                "Change-of-grade seeding skipped: change_of_grade.txt is empty"
            )

            return
        }

        try {
            val insertedRows =
                dbQuery {
                    ChangeOfGrades.batchInsert(
                        data = gradeChanges,
                        shouldReturnGeneratedValues = false
                    ) { gradeName ->
                        this[
                            ChangeOfGrades.grade
                        ] = gradeName
                    }
                }

            println(
                "Change-of-grade seeding completed: " +
                        "${insertedRows.size} row(s) inserted"
            )
        } catch (exception: Exception) {
            println(
                "Change-of-grade seeding failed: ${exception.message}"
            )

            exception.printStackTrace()
        }
    }

    private fun readGradeChangesFromResource():
            List<String> {

        val resourcePath =
            "data/change_of_grade.txt"

        val inputStream =
            ChangeOfGradeSeeder::class.java
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