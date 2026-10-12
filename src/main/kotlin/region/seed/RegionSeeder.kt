package com.hr.region.seed


import com.hr.config.DatabaseFactory.dbQuery
import com.hr.region.tables.Regions
import org.jetbrains.exposed.v1.r2dbc.batchInsert

object RegionSeeder {

    suspend fun seed() {
        val regions =
            try {
                readRegionsFromResource()
            } catch (exception: Exception) {
                println(
                    "Region seeding failed: ${exception.message}"
                )

                return
            }

        if (regions.isEmpty()) {
            println(
                "Region seeding skipped: region.txt is empty"
            )

            return
        }

        try {
            val insertedRows =
                dbQuery {
                    Regions.batchInsert(
                        data = regions,
                        ignore = true,
                        shouldReturnGeneratedValues = false
                    ) { regionName ->
                        this[
                            Regions.region
                        ] = regionName
                    }
                }

            println(
                "Region seeding completed: " +
                        "${insertedRows.size} row(s) inserted"
            )
        } catch (exception: Exception) {
            println(
                "Region seeding failed: ${exception.message}"
            )

            exception.printStackTrace()
        }
    }

    private fun readRegionsFromResource():
            List<String> {

        val resourcePath =
            "data/region.txt"

        val inputStream =
            RegionSeeder::class.java
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