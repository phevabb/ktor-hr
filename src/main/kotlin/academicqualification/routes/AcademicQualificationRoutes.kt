package com.hr.academicqualification.routes

import com.hr.academicqualification.dtos.AcademicQualificationRequest
import com.hr.academicqualification.repositories.AcademicQualificationRepository
import com.hr.config.DatabaseFactory.dbQuery
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.academicQualificationRoutes() {

    get {
        val qualifications = dbQuery {
            AcademicQualificationRepository.getAll()
        }

        call.respond(
            HttpStatusCode.OK,
            qualifications
        )
    }

    get("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Invalid academic qualification ID"
            )

        val qualification = dbQuery {
            AcademicQualificationRepository.getById(id)
        } ?: return@get call.respond(
            HttpStatusCode.NotFound,
            "Academic qualification not found"
        )

        call.respond(
            HttpStatusCode.OK,
            qualification
        )
    }

    post {
        val request = call.receive<AcademicQualificationRequest>()

        val qualification = dbQuery {
            val id = AcademicQualificationRepository.create(request)

            AcademicQualificationRepository.getById(id)
                ?: error(
                    "Academic qualification was created but could not be retrieved"
                )
        }

        call.respond(
            HttpStatusCode.Created,
            qualification
        )
    }

    put("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                "Invalid academic qualification ID"
            )

        val request = call.receive<AcademicQualificationRequest>()

        val qualification = dbQuery {
            val updated = AcademicQualificationRepository.update(
                id = id,
                request = request
            )

            if (updated) {
                AcademicQualificationRepository.getById(id)
            } else {
                null
            }
        } ?: return@put call.respond(
            HttpStatusCode.NotFound,
            "Academic qualification not found"
        )

        call.respond(
            HttpStatusCode.OK,
            qualification
        )
    }


    delete("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                "Invalid academic qualification ID"
            )

        val deleted = dbQuery {
            AcademicQualificationRepository.delete(id)
        }

        if (deleted) {
            call.respond(
                HttpStatusCode.OK,
                "Academic qualification deleted successfully"
            )
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                "Academic qualification not found"
            )
        }
    }
}