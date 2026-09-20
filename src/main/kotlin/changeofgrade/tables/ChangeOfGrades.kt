package com.hr.changeofgrade.tables

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object ChangeOfGrades : IntIdTable("change_of_grades") {

    val grade = varchar(
        name = "grade",
        length = 100
    )
}