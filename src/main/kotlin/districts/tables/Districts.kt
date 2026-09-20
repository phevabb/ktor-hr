package com.hr.districts.tables



import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object Districts : IntIdTable("districts") {

    val district = varchar(
        name = "district",
        length = 120
    )
}