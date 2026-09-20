package com.hr.currentgrade.routes



import com.hr.currentgrade.dtos.CurrentGradeRequest
import com.hr.currentgrade.repo.CurrentGradeRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.currentGradeRoutes() {

    get {

        val currentGrades =
            CurrentGradeRepository.getAll()

        call.respond(
            status = HttpStatusCode.OK,
            message = currentGrades
        )
    }

    get("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Invalid current grade ID"
            )

        val currentGrade =
            CurrentGradeRepository.getById(id)
                ?: return@get call.respond(
                    status = HttpStatusCode.NotFound,
                    message = "Current grade not found"
                )

        call.respond(
            status = HttpStatusCode.OK,
            message = currentGrade
        )
    }

    post {

        val request =
            call.receive<CurrentGradeRequest>()

        val currentGradeName =
            request.currentGrade.trim()

        if (currentGradeName.isBlank()) {
            return@post call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Current grade is required"
            )
        }

        if (currentGradeName.length > 100) {
            return@post call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Current grade cannot exceed 100 characters"
            )
        }

        val existingCurrentGrade =
            CurrentGradeRepository.getByName(currentGradeName)

        if (existingCurrentGrade != null) {
            return@post call.respond(
                status = HttpStatusCode.Conflict,
                message = "Current grade already exists"
            )
        }

        val id = CurrentGradeRepository.create(
            request = request.copy(
                currentGrade = currentGradeName
            )
        )

        val createdCurrentGrade =
            CurrentGradeRepository.getById(id)
                ?: return@post call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = "Current grade was created but could not be retrieved"
                )

        call.respond(
            status = HttpStatusCode.Created,
            message = createdCurrentGrade
        )
    }

    put("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@put call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Invalid current grade ID"
            )

        val existingCurrentGrade =
            CurrentGradeRepository.getById(id)
                ?: return@put call.respond(
                    status = HttpStatusCode.NotFound,
                    message = "Current grade not found"
                )

        val request =
            call.receive<CurrentGradeRequest>()

        val currentGradeName =
            request.currentGrade.trim()

        if (currentGradeName.isBlank()) {
            return@put call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Current grade is required"
            )
        }

        if (currentGradeName.length > 100) {
            return@put call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Current grade cannot exceed 100 characters"
            )
        }

        val currentGradeWithSameName =
            CurrentGradeRepository.getByName(currentGradeName)

        if (
            currentGradeWithSameName != null &&
            currentGradeWithSameName.id != existingCurrentGrade.id
        ) {
            return@put call.respond(
                status = HttpStatusCode.Conflict,
                message = "Current grade already exists"
            )
        }

        val updated = CurrentGradeRepository.update(
            id = id,
            request = request.copy(
                currentGrade = currentGradeName
            )
        )

        if (!updated) {
            return@put call.respond(
                status = HttpStatusCode.NotFound,
                message = "Current grade not found"
            )
        }

        val updatedCurrentGrade =
            CurrentGradeRepository.getById(id)
                ?: return@put call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = "Current grade was updated but could not be retrieved"
                )

        call.respond(
            status = HttpStatusCode.OK,
            message = updatedCurrentGrade
        )
    }

    delete("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Invalid current grade ID"
            )

        val deleted =
            CurrentGradeRepository.delete(id)

        if (!deleted) {
            return@delete call.respond(
                status = HttpStatusCode.NotFound,
                message = "Current grade not found"
            )
        }

        call.respond(
            status = HttpStatusCode.OK,
            message = mapOf(
                "message" to "Current grade deleted successfully"
            )
        )
    }
}