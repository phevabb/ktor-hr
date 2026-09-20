package com.hr.position.table


import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object Positions : IntIdTable("positions") {
    val name = varchar("name", 150).uniqueIndex()
}