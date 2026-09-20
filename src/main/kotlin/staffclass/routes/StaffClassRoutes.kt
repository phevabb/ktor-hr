package com.hr.staffclass.routes

import com.hr.config.DatabaseFactory.dbQuery
import com.hr.staffclass.dtos.StaffClassRequest
import com.hr.staffclass.repositories.StaffClassRepository


import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.staffClassRoutes() {

    get {
        val staffClasses = dbQuery {
            StaffClassRepository.getAll()
        }

        call.respond(
            HttpStatusCode.OK,
            staffClasses
        )
    }

    get("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Invalid staff class ID"
            )

        val staffClass = dbQuery {
            StaffClassRepository.getById(id)
        } ?: return@get call.respond(
            HttpStatusCode.NotFound,
            "Staff class not found"
        )

        call.respond(
            HttpStatusCode.OK,
            staffClass
        )
    }

    post {
        val request = call.receive<StaffClassRequest>()

        if (request.name.isBlank()) {
            return@post call.respond(
                HttpStatusCode.BadRequest,
                "Staff class name is required"
            )
        }

        val staffClass = dbQuery {
            val id = StaffClassRepository.create(request)

            StaffClassRepository.getById(id)
                ?: error(
                    "Staff class was created but could not be retrieved"
                )
        }

        call.respond(
            HttpStatusCode.Created,
            staffClass
        )
    }

    put("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                "Invalid staff class ID"
            )

        val request = call.receive<StaffClassRequest>()

        if (request.name.isBlank()) {
            return@put call.respond(
                HttpStatusCode.BadRequest,
                "Staff class name is required"
            )
        }

        val staffClass = dbQuery {
            val updated = StaffClassRepository.update(
                id = id,
                request = request
            )

            if (updated) {
                StaffClassRepository.getById(id)
            } else {
                null
            }
        } ?: return@put call.respond(
            HttpStatusCode.NotFound,
            "Staff class not found"
        )

        call.respond(
            HttpStatusCode.OK,
            staffClass
        )
    }

    delete("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                "Invalid staff class ID"
            )

        val deleted = dbQuery {
            StaffClassRepository.delete(id)
        }

        if (deleted) {
            call.respond(
                HttpStatusCode.OK,
                "Staff class deleted successfully"
            )
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                "Staff class not found"
            )
        }
    }
}
