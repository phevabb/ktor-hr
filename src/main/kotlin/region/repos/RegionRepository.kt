package com.hr.region.repos


import com.hr.config.DatabaseFactory
import com.hr.region.dtos.RegionRequest
import com.hr.region.dtos.RegionResponse
import com.hr.region.tables.Regions
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update


object RegionRepository {

    suspend fun getById(
        id: Int
    ): RegionResponse? =
        DatabaseFactory.dbQuery {

            Regions
                .selectAll()
                .where { Regions.id eq id }
                .singleOrNull()
                ?.let { row ->
                    RegionResponse(
                        id = row[Regions.id].value,
                        region = row[Regions.region]
                    )
                }
        }
    suspend fun getAll(): List<RegionResponse> =
        DatabaseFactory.dbQuery {

            Regions
                .selectAll()
                .orderBy(
                    Regions.region,
                    SortOrder.ASC
                )
                .map { row ->
                    RegionResponse(
                        id = row[Regions.id].value,
                        region = row[Regions.region]
                    )
                }
                .toList()
        }


    suspend fun create(
        request: RegionRequest
    ): Int =
        DatabaseFactory.dbQuery {

            Regions
                .insertAndGetId {
                    it[region] = request.region
                }
                .value
        }

    suspend fun update(
        id: Int,
        request: RegionRequest
    ): Boolean =
        DatabaseFactory.dbQuery {

            Regions.update(
                { Regions.id eq id }
            ) {
                it[region] = request.region
            } > 0
        }


    suspend fun delete(
        id: Int
    ): Boolean =
        DatabaseFactory.dbQuery {

            Regions.deleteWhere {
                Regions.id eq id
            } > 0
        }
}