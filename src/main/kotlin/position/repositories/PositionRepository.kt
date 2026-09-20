package com.hr.position.repositories


import com.hr.position.dots.PositionRequest
import com.hr.position.dots.PositionResponse
import com.hr.position.table.Positions
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object PositionRepository {

    suspend fun getAll(): List<PositionResponse> {
        return Positions
            .selectAll()
            .orderBy(
                Positions.name,
                SortOrder.ASC
            )
            .map { row ->
                PositionResponse(
                    id = row[Positions.id].value,
                    name = row[Positions.name]
                )
            }
            .toList()
    }

    suspend fun getById(
        id: Int
    ): PositionResponse? {
        return Positions
            .selectAll()
            .where {
                Positions.id eq id
            }
            .singleOrNull()
            ?.let { row ->
                PositionResponse(
                    id = row[Positions.id].value,
                    name = row[Positions.name]
                )
            }
    }

    suspend fun create(
        request: PositionRequest
    ): Int {
        return Positions
            .insertAndGetId {
                it[name] = request.name.trim()
            }
            .value
    }

    suspend fun update(
        id: Int,
        request: PositionRequest
    ): Boolean {
        return Positions.update(
            {
                Positions.id eq id
            }
        ) {
            it[name] = request.name.trim()
        } > 0
    }

    suspend fun delete(
        id: Int
    ): Boolean {
        return Positions.deleteWhere {
            Positions.id eq id
        } > 0
    }
}
