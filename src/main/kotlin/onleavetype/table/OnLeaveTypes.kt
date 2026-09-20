package com.hr.onleavetype.table



import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object OnLeaveTypes : IntIdTable("on_leave_types") {
    val name = varchar("name", 100).uniqueIndex()
}