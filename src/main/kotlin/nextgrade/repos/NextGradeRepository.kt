package com.hr.nextgrade.repos



import com.hr.config.DatabaseFactory
import com.hr.nextgrade.dtos.NextGradeRequest
import com.hr.nextgrade.dtos.NextGradeResponse
import com.hr.nextgrade.tables.NextGrades
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object NextGradeRepository {

    suspend fun getAll(): List<NextGradeResponse> =
        DatabaseFactory.dbQuery {

            NextGrades
                .selectAll()
                .orderBy(
                    NextGrades.nextGrade,
                    SortOrder.ASC
                )
                .map { row ->
                    NextGradeResponse(
                        id = row[NextGrades.id].value,
                        nextGrade = row[
                            NextGrades.nextGrade
                        ]
                    )
                }
                .toList()
        }

    suspend fun getById(
        id: Int
    ): NextGradeResponse? =
        DatabaseFactory.dbQuery {

            NextGrades
                .selectAll()
                .where {
                    NextGrades.id eq id
                }
                .singleOrNull()
                ?.let { row ->
                    NextGradeResponse(
                        id = row[NextGrades.id].value,
                        nextGrade = row[
                            NextGrades.nextGrade
                        ]
                    )
                }
        }

    suspend fun create(
        request: NextGradeRequest
    ): Int =
        DatabaseFactory.dbQuery {

            NextGrades
                .insertAndGetId {
                    it[nextGrade] =
                        request.nextGrade.trim()
                }
                .value
        }

    suspend fun update(
        id: Int,
        request: NextGradeRequest
    ): Boolean =
        DatabaseFactory.dbQuery {

            NextGrades.update(
                where = {
                    NextGrades.id eq id
                }
            ) {
                it[nextGrade] =
                    request.nextGrade.trim()
            } > 0
        }

    suspend fun delete(
        id: Int
    ): Boolean =
        DatabaseFactory.dbQuery {

            NextGrades.deleteWhere {
                NextGrades.id eq id
            } > 0
        }

    suspend fun existsByName(
        nextGrade: String
    ): Boolean =
        DatabaseFactory.dbQuery {

            NextGrades
                .selectAll()
                .where {
                    NextGrades.nextGrade eq
                            nextGrade.trim()
                }
                .singleOrNull() != null
        }

    suspend fun existsByNameExcludingId(
        nextGrade: String,
        excludedId: Int
    ): Boolean =
        DatabaseFactory.dbQuery {

            NextGrades
                .selectAll()
                .where {
                    (
                            NextGrades.nextGrade eq
                                    nextGrade.trim()
                            ) and (
                            NextGrades.id neq excludedId
                            )
                }
                .singleOrNull() != null
        }
}