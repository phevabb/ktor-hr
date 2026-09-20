package com.hr.staffclass.table



import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object StaffClasses : IntIdTable("staff_classes") {
    val name = varchar("name", 120).uniqueIndex()
}