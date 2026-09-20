package com.hr.department.repo

import com.hr.config.DatabaseFactory
import com.hr.department.dto.DepartmentRequest
import com.hr.department.dto.DepartmentResponse
import com.hr.department.table.Departments
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object DepartmentRepository {

    suspend fun getAll(): List<DepartmentResponse> =
        DatabaseFactory.dbQuery {

            Departments
                .selectAll()
                .orderBy(
                    Departments.departmentName,
                    SortOrder.ASC
                )
                .map { row ->
                    DepartmentResponse(
                        id = row[Departments.id].value,
                        departmentName = row[Departments.departmentName]
                    )
                }
                .toList()
        }

    suspend fun getById(
        id: Int
    ): DepartmentResponse? =
        DatabaseFactory.dbQuery {

            Departments
                .selectAll()
                .where {
                    Departments.id eq id
                }
                .singleOrNull()
                ?.let { row ->
                    DepartmentResponse(
                        id = row[Departments.id].value,
                        departmentName = row[Departments.departmentName]
                    )
                }
        }

    suspend fun create(
        request: DepartmentRequest
    ): Int =
        DatabaseFactory.dbQuery {

            Departments
                .insertAndGetId {
                    it[departmentName] = request.departmentName.trim()
                }
                .value
        }

    suspend fun update(
        id: Int,
        request: DepartmentRequest
    ): Boolean =
        DatabaseFactory.dbQuery {

            Departments.update(
                where = {
                    Departments.id eq id
                }
            ) {
                it[departmentName] = request.departmentName.trim()
            } > 0
        }

    suspend fun delete(
        id: Int
    ): Boolean =
        DatabaseFactory.dbQuery {

            Departments.deleteWhere {
                Departments.id eq id
            } > 0
        }
}