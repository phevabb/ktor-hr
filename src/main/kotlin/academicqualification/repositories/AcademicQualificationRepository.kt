package com.hr.academicqualification.repositories



import com.hr.academicqualification.dtos.AcademicQualificationRequest
import com.hr.academicqualification.dtos.AcademicQualificationResponse
import com.hr.academicqualification.table.AcademicQualifications
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object AcademicQualificationRepository {

    suspend fun getAll(): List<AcademicQualificationResponse> {
        return AcademicQualifications
            .selectAll()
            .orderBy(
                AcademicQualifications.name,
                SortOrder.ASC
            )
            .map { row ->
                AcademicQualificationResponse(
                    id = row[AcademicQualifications.id].value,
                    name = row[AcademicQualifications.name]
                )
            }
            .toList()
    }

    suspend fun getById(
        id: Int
    ): AcademicQualificationResponse? {
        return AcademicQualifications
            .selectAll()
            .where {
                AcademicQualifications.id eq id
            }
            .singleOrNull()
            ?.let { row ->
                AcademicQualificationResponse(
                    id = row[AcademicQualifications.id].value,
                    name = row[AcademicQualifications.name]
                )
            }
    }

    suspend fun create(
        request: AcademicQualificationRequest
    ): Int {
        return AcademicQualifications
            .insertAndGetId {
                it[name] = request.name
            }
            .value
    }

    suspend fun update(
        id: Int,
        request: AcademicQualificationRequest
    ): Boolean {
        return AcademicQualifications.update(
            {
                AcademicQualifications.id eq id
            }
        ) {
            it[name] = request.name
        } > 0
    }

    suspend fun delete(
        id: Int
    ): Boolean {
        return AcademicQualifications.deleteWhere {
            AcademicQualifications.id eq id
        } > 0
    }
}