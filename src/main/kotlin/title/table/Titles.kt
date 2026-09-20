package com.hr.title.table



import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object Titles : IntIdTable("titles") {
    val title = varchar("title", 20).uniqueIndex()
}