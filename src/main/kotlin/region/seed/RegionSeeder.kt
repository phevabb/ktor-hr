package com.hr.region.seeder

import com.hr.config.DatabaseFactory.dbQuery
import com.hr.region.tables.Regions
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.insert
import org.jetbrains.exposed.v1.core.selectAll
import org.jetbrains.exposed.v1.r2dbc.insert

object RegionSeeder {

    fun seed() = runBlocking {

        val regions = listOf(
            "Ahafo",
            "Ashanti",
            "Bono",
            "Bono East",
            "Central",
            "Eastern",
            "Greater Accra",
            "North East",
            "Northern",
            "Oti",
            "Savannah",
            "Upper East",
            "Upper West",
            "Volta",
            "Western",
            "Western North"
        )

        dbQuery {

            val existingRegions = Regions
                .selectAll()
                .map { it[Regions.region] }
                .toSet()

            regions.forEach { regionName ->

                if (!existingRegions.contains(regionName)) {

                    Regions.insert {
                        it[region] = regionName
                    }

                    println("Inserted region: $regionName")
                }
            }
        }

        println("Region seeding completed.")
    }
}