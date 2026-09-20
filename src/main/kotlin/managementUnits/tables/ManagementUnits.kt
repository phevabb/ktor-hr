package com.hr.managementUnits.tables



import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object ManagementUnits : IntIdTable("management_units") {

    val managementUnitName = varchar(
        name = "management_unit_name",
        length = 100
    )
}