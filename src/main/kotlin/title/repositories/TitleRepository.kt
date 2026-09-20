package com.hr.title.repositories

import com.hr.title.dtos.TitleRequest
import com.hr.title.dtos.TitleResponse
import com.hr.title.table.Titles
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object TitleRepository {

    suspend fun getAll(): List<TitleResponse> {
        return Titles
            .selectAll()
            .orderBy(
                Titles.title,
                SortOrder.ASC
            )
            .map { row ->
                TitleResponse(
                    id = row[Titles.id].value,
                    title = row[Titles.title]
                )
            }
            .toList()
    }

    suspend fun getById(
        id: Int
    ): TitleResponse? {
        return Titles
            .selectAll()
            .where {
                Titles.id eq id
            }
            .singleOrNull()
            ?.let { row ->
                TitleResponse(
                    id = row[Titles.id].value,
                    title = row[Titles.title]
                )
            }
    }

    suspend fun create(
        request: TitleRequest
    ): Int {
        return Titles
            .insertAndGetId {
                it[title] = request.title.trim()
            }
            .value
    }

    suspend fun update(
        id: Int,
        request: TitleRequest
    ): Boolean {
        return Titles.update(
            {
                Titles.id eq id
            }
        ) {
            it[title] = request.title.trim()
        } > 0
    }

    suspend fun delete(
        id: Int
    ): Boolean {
        return Titles.deleteWhere {
            Titles.id eq id
        } > 0
    }
}