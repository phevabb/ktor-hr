package com.hr.staffclass.repositories


import com.hr.staffclass.dtos.StaffClassRequest
import com.hr.staffclass.dtos.StaffClassResponse
import com.hr.staffclass.table.StaffClasses
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object StaffClassRepository {

    suspend fun getAll(): List<StaffClassResponse> {
        return StaffClasses
            .selectAll()
            .orderBy(
                StaffClasses.name,
                SortOrder.ASC
            )
            .map { row ->
                StaffClassResponse(
                    id = row[StaffClasses.id].value,
                    name = row[StaffClasses.name]
                )
            }
            .toList()
    }

    suspend fun getById(
        id: Int
    ): StaffClassResponse? {
        return StaffClasses
            .selectAll()
            .where {
                StaffClasses.id eq id
            }
            .singleOrNull()
            ?.let { row ->
                StaffClassResponse(
                    id = row[StaffClasses.id].value,
                    name = row[StaffClasses.name]
                )
            }
    }

    suspend fun create(
        request: StaffClassRequest
    ): Int {
        return StaffClasses
            .insertAndGetId {
                it[name] = request.name.trim()
            }
            .value
    }

    suspend fun update(
        id: Int,
        request: StaffClassRequest
    ): Boolean {
        return StaffClasses.update(
            {
                StaffClasses.id eq id
            }
        ) {
            it[name] = request.name.trim()
        } > 0
    }

    suspend fun delete(
        id: Int
    ): Boolean {
        return StaffClasses.deleteWhere {
            StaffClasses.id eq id
        } > 0
    }
}