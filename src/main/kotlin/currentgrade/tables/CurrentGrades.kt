package com.hr.currentgrade.tables



import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object CurrentGrades : IntIdTable("current_grades") {

    val currentGrade = varchar(
        name = "current_grade",
        length = 100
    ).uniqueIndex()
}