package com.hr.nextgrade.seed

import com.hr.config.DatabaseFactory.dbQuery
import com.hr.nextgrade.tables.NextGrades
import org.jetbrains.exposed.v1.r2dbc.batchInsert

object NextGradeSeeder {

    suspend fun seed() {
        val nextGrades =
            try {
                readNextGradesFromResource()
            } catch (exception: Exception) {
                println(
                    "Next-grade seeding failed: ${exception.message}"
                )

                return
            }

        if (nextGrades.isEmpty()) {
            println(
                "Next-grade seeding skipped: next_grade.txt is empty"
            )

            return
        }

        try {
            val insertedRows =
                dbQuery {
                    NextGrades.batchInsert(
                        data = nextGrades,
                        ignore = true,
                        shouldReturnGeneratedValues = false
                    ) { nextGradeName ->
                        this[
                            NextGrades.nextGrade
                        ] = nextGradeName
                    }
                }

            println(
                "Next-grade seeding completed: " +
                        "${insertedRows.size} row(s) inserted"
            )
        } catch (exception: Exception) {
            println(
                "Next-grade seeding failed: ${exception.message}"
            )

            exception.printStackTrace()
        }
    }

    private fun readNextGradesFromResource():
            List<String> {

        val resourcePath =
            "data/next_grade.txt"

        val inputStream =
            NextGradeSeeder::class.java
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