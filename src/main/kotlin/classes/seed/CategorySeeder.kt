package com.hr.classes.seed

import com.hr.classes.tables.Classes
import com.hr.config.DatabaseFactory.dbQuery

import org.jetbrains.exposed.v1.r2dbc.batchInsert

object CategorySeeder {

    suspend fun seed() {
        val categories =
            try {
                readCategoriesFromResource()
            } catch (exception: Exception) {
                println(
                    "Category seeding failed: ${exception.message}"
                )

                return
            }

        if (categories.isEmpty()) {
            println(
                "Category seeding skipped: category.txt is empty"
            )

            return
        }

        try {
            val insertedRows =
                dbQuery {
                    Classes.batchInsert(
                        data = categories,
                        ignore = true,
                        shouldReturnGeneratedValues = false
                    ) { categoryName ->
                        this[
                            Classes.classesName
                        ] = categoryName
                    }
                }

            println(
                "Category seeding completed: " +
                        "${insertedRows.size} row(s) inserted"
            )
        } catch (exception: Exception) {
            println(
                "Category seeding failed: ${exception.message}"
            )

            exception.printStackTrace()
        }
    }

    private fun readCategoriesFromResource():
            List<String> {

        val resourcePath =
            "data/category.txt"

        val inputStream =
            CategorySeeder::class.java
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