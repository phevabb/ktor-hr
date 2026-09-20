package com.hr.title.routes


import com.hr.config.DatabaseFactory.dbQuery
import com.hr.title.dtos.TitleRequest
import com.hr.title.repositories.TitleRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.titleRoutes() {

    get {
        val titles = dbQuery {
            TitleRepository.getAll()
        }

        call.respond(
            HttpStatusCode.OK,
            titles
        )
    }

    get("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Invalid title ID"
            )

        val title = dbQuery {
            TitleRepository.getById(id)
        } ?: return@get call.respond(
            HttpStatusCode.NotFound,
            "Title not found"
        )

        call.respond(
            HttpStatusCode.OK,
            title
        )
    }

    post {
        val request = call.receive<TitleRequest>()

        if (request.title.isBlank()) {
            return@post call.respond(
                HttpStatusCode.BadRequest,
                "Title is required"
            )
        }

        val title = dbQuery {
            val id = TitleRepository.create(request)

            TitleRepository.getById(id)
                ?: error(
                    "Title was created but could not be retrieved"
                )
        }

        call.respond(
            HttpStatusCode.Created,
            title
        )
    }

    put("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                "Invalid title ID"
            )

        val request = call.receive<TitleRequest>()

        if (request.title.isBlank()) {
            return@put call.respond(
                HttpStatusCode.BadRequest,
                "Title is required"
            )
        }

        val title = dbQuery {
            val updated = TitleRepository.update(
                id = id,
                request = request
            )

            if (updated) {
                TitleRepository.getById(id)
            } else {
                null
            }
        } ?: return@put call.respond(
            HttpStatusCode.NotFound,
            "Title not found"
        )

        call.respond(
            HttpStatusCode.OK,
            title
        )
    }

    delete("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                "Invalid title ID"
            )

        val deleted = dbQuery {
            TitleRepository.delete(id)
        }

        if (deleted) {
            call.respond(
                HttpStatusCode.OK,
                "Title deleted successfully"
            )
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                "Title not found"
            )
        }
    }
}