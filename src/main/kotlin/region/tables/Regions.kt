package com.hr.region.tables



import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object Regions : IntIdTable("regions") {
    val region = varchar("region", 120)
}