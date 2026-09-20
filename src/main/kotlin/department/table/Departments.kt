package com.hr.department.table



import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object Departments : IntIdTable("departments") {

    val departmentName = varchar(
        name = "department_name",
        length = 120
    )
}
