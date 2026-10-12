package com.hr.districts.seed

import com.hr.config.DatabaseFactory.dbQuery
import com.hr.districts.tables.Districts
import org.jetbrains.exposed.v1.r2dbc.batchInsert

object DistrictSeeder {

    suspend fun seed() {
        val districts =
            try {
                readDistrictsFromResource()
            } catch (exception: Exception) {
                println(
                    "District seeding failed: ${exception.message}"
                )

                return
            }

        if (districts.isEmpty()) {
            println(
                "District seeding skipped: district.txt is empty"
            )

            return
        }

        try {
            val insertedRows =
                dbQuery {
                    Districts.batchInsert(
                        data = districts,
                        ignore = true,
                        shouldReturnGeneratedValues = false
                    ) { districtName ->
                        this[
                            Districts.district
                        ] = districtName
                    }
                }

            println(
                "District seeding completed: " +
                        "${insertedRows.size} row(s) inserted"
            )
        } catch (exception: Exception) {
            println(
                "District seeding failed: ${exception.message}"
            )

            exception.printStackTrace()
        }
    }

    private fun readDistrictsFromResource():
            List<String> {

        val resourcePath =
            "data/district.txt"

        val inputStream =
            DistrictSeeder::class.java
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