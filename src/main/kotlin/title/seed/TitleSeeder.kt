package com.hr.title.seed

import com.hr.config.DatabaseFactory.dbQuery
import com.hr.title.table.Titles
import org.jetbrains.exposed.v1.r2dbc.batchInsert

object TitleSeeder {

    suspend fun seed() {
        val titles =
            try {
                readTitlesFromResource()
            } catch (exception: Exception) {
                println(
                    "Title seeding failed: ${exception.message}"
                )

                return
            }

        if (titles.isEmpty()) {
            println(
                "Title seeding skipped: title.txt is empty"
            )

            return
        }

        try {
            val insertedRows =
                dbQuery {
                    Titles.batchInsert(
                        data = titles,
                        ignore = true,
                        shouldReturnGeneratedValues = false
                    ) { titleName ->
                        this[
                            Titles.title
                        ] = titleName
                    }
                }

            println(
                "Title seeding completed: " +
                        "${insertedRows.size} row(s) inserted"
            )
        } catch (exception: Exception) {
            println(
                "Title seeding failed: ${exception.message}"
            )

            exception.printStackTrace()
        }
    }

    private fun readTitlesFromResource():
            List<String> {

        val resourcePath =
            "data/title.txt"

        val inputStream =
            TitleSeeder::class.java
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
                        line.length <= 20
                    }
                    .distinctBy { line ->
                        line.lowercase()
                    }
                    .toList()
            }
    }
}