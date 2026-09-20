package com.hr.currentgrade.repo


import com.hr.config.DatabaseFactory
import com.hr.currentgrade.dtos.CurrentGradeRequest
import com.hr.currentgrade.dtos.CurrentGradeResponse
import com.hr.currentgrade.tables.CurrentGrades
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object CurrentGradeRepository {

    suspend fun getAll(): List<CurrentGradeResponse> =
        DatabaseFactory.dbQuery {

            CurrentGrades
                .selectAll()
                .orderBy(
                    CurrentGrades.currentGrade,
                    SortOrder.ASC
                )
                .map { row ->
                    CurrentGradeResponse(
                        id = row[CurrentGrades.id].value,
                        currentGrade = row[CurrentGrades.currentGrade]
                    )
                }
                .toList()
        }

    suspend fun getById(
        id: Int
    ): CurrentGradeResponse? =
        DatabaseFactory.dbQuery {

            CurrentGrades
                .selectAll()
                .where {
                    CurrentGrades.id eq id
                }
                .singleOrNull()
                ?.let { row ->
                    CurrentGradeResponse(
                        id = row[CurrentGrades.id].value,
                        currentGrade = row[CurrentGrades.currentGrade]
                    )
                }
        }

    suspend fun getByName(
        currentGrade: String
    ): CurrentGradeResponse? =
        DatabaseFactory.dbQuery {

            CurrentGrades
                .selectAll()
                .where {
                    CurrentGrades.currentGrade eq currentGrade.trim()
                }
                .singleOrNull()
                ?.let { row ->
                    CurrentGradeResponse(
                        id = row[CurrentGrades.id].value,
                        currentGrade = row[CurrentGrades.currentGrade]
                    )
                }
        }

    suspend fun create(
        request: CurrentGradeRequest
    ): Int =
        DatabaseFactory.dbQuery {

            CurrentGrades
                .insertAndGetId {
                    it[currentGrade] = request.currentGrade.trim()
                }
                .value
        }

    suspend fun update(
        id: Int,
        request: CurrentGradeRequest
    ): Boolean =
        DatabaseFactory.dbQuery {

            CurrentGrades.update(
                where = {
                    CurrentGrades.id eq id
                }
            ) {
                it[currentGrade] = request.currentGrade.trim()
            } > 0
        }

    suspend fun delete(
        id: Int
    ): Boolean =
        DatabaseFactory.dbQuery {

            CurrentGrades.deleteWhere {
                CurrentGrades.id eq id
            } > 0
        }
}