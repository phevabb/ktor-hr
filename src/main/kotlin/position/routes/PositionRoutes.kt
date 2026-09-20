package com.hr.position.routes


import com.hr.config.DatabaseFactory.dbQuery
import com.hr.position.dots.PositionRequest
import com.hr.position.repositories.PositionRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.positionRoutes() {

    get {
        val positions = dbQuery {
            PositionRepository.getAll()
        }

        call.respond(
            HttpStatusCode.OK,
            positions
        )
    }

    get("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Invalid position ID"
            )

        val position = dbQuery {
            PositionRepository.getById(id)
        } ?: return@get call.respond(
            HttpStatusCode.NotFound,
            "Position not found"
        )

        call.respond(
            HttpStatusCode.OK,
            position
        )
    }

    post {
        val request = call.receive<PositionRequest>()

        if (request.name.isBlank()) {
            return@post call.respond(
                HttpStatusCode.BadRequest,
                "Position name is required"
            )
        }

        val position = dbQuery {
            val id = PositionRepository.create(request)

            PositionRepository.getById(id)
                ?: error(
                    "Position was created but could not be retrieved"
                )
        }

        call.respond(
            HttpStatusCode.Created,
            position
        )
    }

    put("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                "Invalid position ID"
            )

        val request = call.receive<PositionRequest>()

        if (request.name.isBlank()) {
            return@put call.respond(
                HttpStatusCode.BadRequest,
                "Position name is required"
            )
        }

        val position = dbQuery {
            val updated = PositionRepository.update(
                id = id,
                request = request
            )

            if (updated) {
                PositionRepository.getById(id)
            } else {
                null
            }
        } ?: return@put call.respond(
            HttpStatusCode.NotFound,
            "Position not found"
        )

        call.respond(
            HttpStatusCode.OK,
            position
        )
    }

    delete("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                "Invalid position ID"
            )

        val deleted = dbQuery {
            PositionRepository.delete(id)
        }

        if (deleted) {
            call.respond(
                HttpStatusCode.OK,
                "Position deleted successfully"
            )
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                "Position not found"
            )
        }
    }
}
