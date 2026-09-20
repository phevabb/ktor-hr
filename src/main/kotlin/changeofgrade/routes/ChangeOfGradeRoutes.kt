package com.hr.changeofgrade.routes



import com.hr.changeofgrade.dtos.ChangeOfGradeRequest
import com.hr.changeofgrade.repo.ChangeOfGradeRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.changeOfGradeRoutes() {

    get {

        val changesOfGrade =
            ChangeOfGradeRepository.getAll()

        call.respond(
            status = HttpStatusCode.OK,
            message = changesOfGrade
        )
    }

    get("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Invalid change of grade ID"
            )

        val changeOfGrade =
            ChangeOfGradeRepository.getById(id)
                ?: return@get call.respond(
                    status = HttpStatusCode.NotFound,
                    message = "Change of grade not found"
                )

        call.respond(
            status = HttpStatusCode.OK,
            message = changeOfGrade
        )
    }

    post {

        val request =
            call.receive<ChangeOfGradeRequest>()

        val grade = request.grade.trim()

        if (grade.isBlank()) {
            return@post call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Grade is required"
            )
        }

        if (grade.length > 100) {
            return@post call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Grade cannot exceed 100 characters"
            )
        }

        val id = ChangeOfGradeRepository.create(
            request = request.copy(
                grade = grade
            )
        )

        val createdChangeOfGrade =
            ChangeOfGradeRepository.getById(id)
                ?: return@post call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = "Change of grade was created but could not be retrieved"
                )

        call.respond(
            status = HttpStatusCode.Created,
            message = createdChangeOfGrade
        )
    }

    put("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@put call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Invalid change of grade ID"
            )

        val existingChangeOfGrade =
            ChangeOfGradeRepository.getById(id)
                ?: return@put call.respond(
                    status = HttpStatusCode.NotFound,
                    message = "Change of grade not found"
                )

        val request =
            call.receive<ChangeOfGradeRequest>()

        val grade = request.grade.trim()

        if (grade.isBlank()) {
            return@put call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Grade is required"
            )
        }

        if (grade.length > 100) {
            return@put call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Grade cannot exceed 100 characters"
            )
        }

        val updated = ChangeOfGradeRepository.update(
            id = existingChangeOfGrade.id,
            request = request.copy(
                grade = grade
            )
        )

        if (!updated) {
            return@put call.respond(
                status = HttpStatusCode.NotFound,
                message = "Change of grade not found"
            )
        }

        val updatedChangeOfGrade =
            ChangeOfGradeRepository.getById(id)
                ?: return@put call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = "Change of grade was updated but could not be retrieved"
                )

        call.respond(
            status = HttpStatusCode.OK,
            message = updatedChangeOfGrade
        )
    }

    delete("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Invalid change of grade ID"
            )

        val deleted =
            ChangeOfGradeRepository.delete(id)

        if (!deleted) {
            return@delete call.respond(
                status = HttpStatusCode.NotFound,
                message = "Change of grade not found"
            )
        }

        call.respond(
            status = HttpStatusCode.OK,
            message = mapOf(
                "message" to "Change of grade deleted successfully"
            )
        )
    }
}
