package com.hr.districts.repos



import com.hr.config.DatabaseFactory
import com.hr.districts.dtos.DistrictRequest
import com.hr.districts.dtos.DistrictResponse
import com.hr.districts.tables.Districts
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList

import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object DistrictRepository {

    suspend fun getAll(): List<DistrictResponse> =
        DatabaseFactory.dbQuery {

            Districts
                .selectAll()
                .orderBy(
                    Districts.district,
                    SortOrder.ASC
                )
                .map { row ->
                    DistrictResponse(
                        id = row[Districts.id].value,
                        district = row[Districts.district]
                    )
                }
                .toList()
        }

    suspend fun getById(
        id: Int
    ): DistrictResponse? =
        DatabaseFactory.dbQuery {

            Districts
                .selectAll()
                .where {
                    Districts.id eq id
                }
                .singleOrNull()
                ?.let { row ->
                    DistrictResponse(
                        id = row[Districts.id].value,
                        district = row[Districts.district]
                    )
                }
        }

    suspend fun create(
        request: DistrictRequest
    ): Int =
        DatabaseFactory.dbQuery {

            Districts
                .insertAndGetId {
                    it[district] = request.district
                }
                .value
        }

    suspend fun update(
        id: Int,
        request: DistrictRequest
    ): Boolean =
        DatabaseFactory.dbQuery {

            Districts.update(
                {
                    Districts.id eq id
                }
            ) {
                it[district] = request.district
            } > 0
        }

    suspend fun delete(
        id: Int
    ): Boolean =
        DatabaseFactory.dbQuery {

            Districts.deleteWhere {
                Districts.id eq id
            } > 0
        }
}