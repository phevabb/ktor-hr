package com.hr.managementUnits.repo


import com.hr.config.DatabaseFactory
import com.hr.managementUnits.tables.ManagementUnits
import com.hr.managementunit.dtos.ManagementUnitRequest
import com.hr.managementunit.dtos.ManagementUnitResponse
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList

import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object ManagementUnitRepository {

    suspend fun getAll(): List<ManagementUnitResponse> =
        DatabaseFactory.dbQuery {

            ManagementUnits
                .selectAll()
                .orderBy(
                    ManagementUnits.managementUnitName,
                    SortOrder.ASC
                )
                .map { row ->
                    ManagementUnitResponse(
                        id = row[ManagementUnits.id].value,
                        managementUnitName =
                            row[ManagementUnits.managementUnitName]
                    )
                }
                .toList()
        }

    suspend fun getById(
        id: Int
    ): ManagementUnitResponse? =
        DatabaseFactory.dbQuery {

            ManagementUnits
                .selectAll()
                .where {
                    ManagementUnits.id eq id
                }
                .singleOrNull()
                ?.let { row ->
                    ManagementUnitResponse(
                        id = row[ManagementUnits.id].value,
                        managementUnitName =
                            row[ManagementUnits.managementUnitName]
                    )
                }
        }

    suspend fun create(
        request: ManagementUnitRequest
    ): Int =
        DatabaseFactory.dbQuery {

            ManagementUnits
                .insertAndGetId {
                    it[managementUnitName] =
                        request.managementUnitName.trim()
                }
                .value
        }

    suspend fun update(
        id: Int,
        request: ManagementUnitRequest
    ): Boolean =
        DatabaseFactory.dbQuery {

            ManagementUnits.update(
                where = {
                    ManagementUnits.id eq id
                }
            ) {
                it[managementUnitName] =
                    request.managementUnitName.trim()
            } > 0
        }

    suspend fun delete(
        id: Int
    ): Boolean =
        DatabaseFactory.dbQuery {

            ManagementUnits.deleteWhere {
                ManagementUnits.id eq id
            } > 0
        }
}