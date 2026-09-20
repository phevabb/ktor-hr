package com.hr.onleavetype.routes


import com.hr.config.DatabaseFactory.dbQuery
import com.hr.onleavetype.dtos.OnLeaveTypeRequest
import com.hr.onleavetype.repositories.OnLeaveTypeRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.onLeaveTypeRoutes() {

    get {
        val leaveTypes = dbQuery {
            OnLeaveTypeRepository.getAll()
        }

        call.respond(
            HttpStatusCode.OK,
            leaveTypes
        )
    }

    get("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Invalid leave type ID"
            )

        val leaveType = dbQuery {
            OnLeaveTypeRepository.getById(id)
        } ?: return@get call.respond(
            HttpStatusCode.NotFound,
            "Leave type not found"
        )

        call.respond(
            HttpStatusCode.OK,
            leaveType
        )
    }

    post {
        val request = call.receive<OnLeaveTypeRequest>()

        if (request.name.isBlank()) {
            return@post call.respond(
                HttpStatusCode.BadRequest,
                "Leave type name is required"
            )
        }

        val leaveType = dbQuery {
            val id = OnLeaveTypeRepository.create(request)

            OnLeaveTypeRepository.getById(id)
                ?: error(
                    "Leave type was created but could not be retrieved"
                )
        }

        call.respond(
            HttpStatusCode.Created,
            leaveType
        )
    }

    put("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                "Invalid leave type ID"
            )

        val request = call.receive<OnLeaveTypeRequest>()

        if (request.name.isBlank()) {
            return@put call.respond(
                HttpStatusCode.BadRequest,
                "Leave type name is required"
            )
        }

        val leaveType = dbQuery {
            val updated = OnLeaveTypeRepository.update(
                id = id,
                request = request
            )

            if (updated) {
                OnLeaveTypeRepository.getById(id)
            } else {
                null
            }
        } ?: return@put call.respond(
            HttpStatusCode.NotFound,
            "Leave type not found"
        )

        call.respond(
            HttpStatusCode.OK,
            leaveType
        )
    }

    delete("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                "Invalid leave type ID"
            )

        val deleted = dbQuery {
            OnLeaveTypeRepository.delete(id)
        }

        if (deleted) {
            call.respond(
                HttpStatusCode.OK,
                "Leave type deleted successfully"
            )
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                "Leave type not found"
            )
        }
    }
}