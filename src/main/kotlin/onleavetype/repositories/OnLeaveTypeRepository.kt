package com.hr.onleavetype.repositories


import com.hr.onleavetype.dtos.OnLeaveTypeRequest
import com.hr.onleavetype.dtos.OnLeaveTypeResponse
import com.hr.onleavetype.table.OnLeaveTypes
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object OnLeaveTypeRepository {

    suspend fun getAll(): List<OnLeaveTypeResponse> {
        return OnLeaveTypes
            .selectAll()
            .orderBy(
                OnLeaveTypes.name,
                SortOrder.ASC
            )
            .map { row ->
                OnLeaveTypeResponse(
                    id = row[OnLeaveTypes.id].value,
                    name = row[OnLeaveTypes.name]
                )
            }
            .toList()
    }

    suspend fun getById(
        id: Int
    ): OnLeaveTypeResponse? {
        return OnLeaveTypes
            .selectAll()
            .where {
                OnLeaveTypes.id eq id
            }
            .singleOrNull()
            ?.let { row ->
                OnLeaveTypeResponse(
                    id = row[OnLeaveTypes.id].value,
                    name = row[OnLeaveTypes.name]
                )
            }
    }

    suspend fun create(
        request: OnLeaveTypeRequest
    ): Int {
        return OnLeaveTypes
            .insertAndGetId {
                it[name] = request.name.trim()
            }
            .value
    }

    suspend fun update(
        id: Int,
        request: OnLeaveTypeRequest
    ): Boolean {
        return OnLeaveTypes.update(
            {
                OnLeaveTypes.id eq id
            }
        ) {
            it[name] = request.name.trim()
        } > 0
    }

    suspend fun delete(
        id: Int
    ): Boolean {
        return OnLeaveTypes.deleteWhere {
            OnLeaveTypes.id eq id
        } > 0
    }
}