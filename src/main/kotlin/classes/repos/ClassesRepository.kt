package com.hr.classes.repos



import com.hr.classes.dtos.ClassesRequest
import com.hr.classes.dtos.ClassesResponse
import com.hr.classes.tables.Classes
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

object ClassesRepository {

    suspend fun getAll(): List<ClassesResponse> =
        DatabaseFactory.dbQuery {

            Classes
                .selectAll()
                .orderBy(
                    Classes.classesName,
                    SortOrder.ASC
                )
                .map { row ->
                    ClassesResponse(
                        id = row[Classes.id].value,
                        classesName = row[Classes.classesName]
                    )
                }
                .toList()
        }

    suspend fun getById(
        id: Int
    ): ClassesResponse? =
        DatabaseFactory.dbQuery {

            Classes
                .selectAll()
                .where {
                    Classes.id eq id
                }
                .singleOrNull()
                ?.let { row ->
                    ClassesResponse(
                        id = row[Classes.id].value,
                        classesName = row[Classes.classesName]
                    )
                }
        }

    suspend fun getByName(
        classesName: String
    ): ClassesResponse? =
        DatabaseFactory.dbQuery {

            Classes
                .selectAll()
                .where {
                    Classes.classesName eq classesName.trim()
                }
                .singleOrNull()
                ?.let { row ->
                    ClassesResponse(
                        id = row[Classes.id].value,
                        classesName = row[Classes.classesName]
                    )
                }
        }

    suspend fun existsByName(
        classesName: String
    ): Boolean =
        DatabaseFactory.dbQuery {

            Classes
                .selectAll()
                .where {
                    Classes.classesName eq classesName.trim()
                }
                .singleOrNull() != null
        }

    suspend fun create(
        request: ClassesRequest
    ): Int =
        DatabaseFactory.dbQuery {

            Classes
                .insertAndGetId {
                    it[classesName] = request.classesName.trim()
                }
                .value
        }

    suspend fun update(
        id: Int,
        request: ClassesRequest
    ): Boolean =
        DatabaseFactory.dbQuery {

            Classes.update(
                where = {
                    Classes.id eq id
                }
            ) {
                it[classesName] = request.classesName.trim()
            } > 0
        }

    suspend fun delete(
        id: Int
    ): Boolean =
        DatabaseFactory.dbQuery {

            Classes.deleteWhere {
                Classes.id eq id
            } > 0
        }
}