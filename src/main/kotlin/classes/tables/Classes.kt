package com.hr.classes.tables


import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object Classes : IntIdTable("classes") {

    val classesName = varchar(
        name = "classes_name",
        length = 100
    ).uniqueIndex()
}
