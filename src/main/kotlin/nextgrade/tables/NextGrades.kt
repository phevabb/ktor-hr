package com.hr.nextgrade.tables



import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object NextGrades : IntIdTable("next_grades") {

    val nextGrade = varchar(
        name = "next_grade",
        length = 100
    ).uniqueIndex()
}