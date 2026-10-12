package com.hr.academicqualification.seed

import com.hr.academicqualification.table.AcademicQualifications
import com.hr.config.DatabaseFactory.dbQuery

import org.jetbrains.exposed.v1.r2dbc.batchInsert

object AcademicQualificationSeeder {

    suspend fun seed() {
        println(
            "=================================================="
        )

        println(
            "Academic qualification seeding started"
        )

        val qualifications =
            try {
                readQualificationsFromResource()
            } catch (exception: Exception) {
                println(
                    "Unable to read academic qualifications resource file"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
                )

                exception.printStackTrace()

                println(
                    "=================================================="
                )

                return
            }

        if (qualifications.isEmpty()) {
            println(
                "No academic qualifications were found in the resource file"
            )

            println(
                "=================================================="
            )

            return
        }

        println(
            "Qualifications found in file: ${qualifications.size}"
        )

        try {
            val insertedRows =
                dbQuery {
                    AcademicQualifications.batchInsert(
                        data = qualifications,
                        ignore = true,
                        shouldReturnGeneratedValues = false
                    ) { qualificationName ->
                        this[
                            AcademicQualifications.name
                        ] = qualificationName
                    }
                }

            println(
                "Academic qualification seeding completed"
            )

            println(
                "Rows reported by batch insert: ${insertedRows.size}"
            )
        } catch (exception: Exception) {
            println(
                "Academic qualification seeding failed"
            )

            println(
                "Error type: ${exception::class.simpleName}"
            )

            println(
                "Error message: ${exception.message}"
            )

            exception.cause?.let { cause ->
                println(
                    "Cause type: ${cause::class.simpleName}"
                )

                println(
                    "Cause message: ${cause.message}"
                )
            }

            exception.printStackTrace()
        }

        println(
            "=================================================="
        )
    }

    private fun readQualificationsFromResource():
            List<String> {

        val resourcePath =
            "data/academic_qualifications.txt"

        val classLoader =
            AcademicQualificationSeeder::class.java
                .classLoader

        val inputStream =
            classLoader.getResourceAsStream(
                resourcePath
            )
                ?: throw IllegalStateException(
                    "Resource file was not found: $resourcePath"
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