package com.hr.changeofgrade.repo

import com.hr.changeofgrade.dtos.ChangeOfGradeRequest
import com.hr.changeofgrade.dtos.ChangeOfGradeResponse
import com.hr.changeofgrade.tables.ChangeOfGrades
import com.hr.config.DatabaseFactory
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object ChangeOfGradeRepository {

    suspend fun getAll(): List<ChangeOfGradeResponse> =
        DatabaseFactory.dbQuery {

            ChangeOfGrades
                .selectAll()
                .orderBy(
                    ChangeOfGrades.grade,
                    SortOrder.ASC
                )
                .map { row ->
                    ChangeOfGradeResponse(
                        id = row[ChangeOfGrades.id].value,
                        grade = row[ChangeOfGrades.grade]
                    )
                }
                .toList()
        }

    suspend fun getById(
        id: Int
    ): ChangeOfGradeResponse? =
        DatabaseFactory.dbQuery {

            ChangeOfGrades
                .selectAll()
                .where {
                    ChangeOfGrades.id eq id
                }
                .singleOrNull()
                ?.let { row ->
                    ChangeOfGradeResponse(
                        id = row[ChangeOfGrades.id].value,
                        grade = row[ChangeOfGrades.grade]
                    )
                }
        }

    suspend fun create(
        request: ChangeOfGradeRequest
    ): Int =
        DatabaseFactory.dbQuery {

            ChangeOfGrades
                .insertAndGetId {
                    it[grade] = request.grade.trim()
                }
                .value
        }

    suspend fun update(
        id: Int,
        request: ChangeOfGradeRequest
    ): Boolean =
        DatabaseFactory.dbQuery {

            ChangeOfGrades.update(
                where = {
                    ChangeOfGrades.id eq id
                }
            ) {
                it[grade] = request.grade.trim()
            } > 0
        }

    suspend fun delete(
        id: Int
    ): Boolean =
        DatabaseFactory.dbQuery {

            ChangeOfGrades.deleteWhere {
                ChangeOfGrades.id eq id
            } > 0
        }
}
