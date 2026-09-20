package com.hr.managementUnits.routes



import com.hr.managementUnits.repo.ManagementUnitRepository
import com.hr.managementunit.dtos.ManagementUnitRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.managementUnitRoutes() {

    get {

        val managementUnits =
            ManagementUnitRepository.getAll()

        call.respond(
            status = HttpStatusCode.OK,
            message = managementUnits
        )
    }

    get("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Invalid management unit ID"
            )

        val managementUnit =
            ManagementUnitRepository.getById(id)
                ?: return@get call.respond(
                    status = HttpStatusCode.NotFound,
                    message = "Management unit not found"
                )

        call.respond(
            status = HttpStatusCode.OK,
            message = managementUnit
        )
    }

    post {

        val request =
            call.receive<ManagementUnitRequest>()

        val managementUnitName =
            request.managementUnitName.trim()

        if (managementUnitName.isBlank()) {
            return@post call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Management unit name is required"
            )
        }

        if (managementUnitName.length > 100) {
            return@post call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Management unit name cannot exceed 100 characters"
            )
        }

        val id = ManagementUnitRepository.create(
            request = request.copy(
                managementUnitName = managementUnitName
            )
        )

        val createdManagementUnit =
            ManagementUnitRepository.getById(id)
                ?: return@post call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = "Management unit was created but could not be retrieved"
                )

        call.respond(
            status = HttpStatusCode.Created,
            message = createdManagementUnit
        )
    }

    put("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@put call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Invalid management unit ID"
            )

        val existingManagementUnit =
            ManagementUnitRepository.getById(id)
                ?: return@put call.respond(
                    status = HttpStatusCode.NotFound,
                    message = "Management unit not found"
                )

        val request =
            call.receive<ManagementUnitRequest>()

        val managementUnitName =
            request.managementUnitName.trim()

        if (managementUnitName.isBlank()) {
            return@put call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Management unit name is required"
            )
        }

        if (managementUnitName.length > 100) {
            return@put call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Management unit name cannot exceed 100 characters"
            )
        }

        val updated = ManagementUnitRepository.update(
            id = existingManagementUnit.id,
            request = request.copy(
                managementUnitName = managementUnitName
            )
        )

        if (!updated) {
            return@put call.respond(
                status = HttpStatusCode.NotFound,
                message = "Management unit not found"
            )
        }

        val updatedManagementUnit =
            ManagementUnitRepository.getById(id)
                ?: return@put call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = "Management unit was updated but could not be retrieved"
                )

        call.respond(
            status = HttpStatusCode.OK,
            message = updatedManagementUnit
        )
    }

    delete("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Invalid management unit ID"
            )

        val deleted =
            ManagementUnitRepository.delete(id)

        if (!deleted) {
            return@delete call.respond(
                status = HttpStatusCode.NotFound,
                message = "Management unit not found"
            )
        }

        call.respond(
            status = HttpStatusCode.OK,
            message = mapOf(
                "message" to
                        "Management unit deleted successfully"
            )
        )
    }
}