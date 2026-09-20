package com.hr.classes.routes


import com.hr.classes.dtos.ClassesRequest
import com.hr.classes.repos.ClassesRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.classesRoutes() {

    get {

        val classes = ClassesRepository.getAll()

        call.respond(
            status = HttpStatusCode.OK,
            message = classes
        )
    }

    get("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Invalid class ID"
            )

        val classes = ClassesRepository.getById(id)
            ?: return@get call.respond(
                status = HttpStatusCode.NotFound,
                message = "Class not found"
            )

        call.respond(
            status = HttpStatusCode.OK,
            message = classes
        )
    }

    post {

        val request = call.receive<ClassesRequest>()

        val classesName = request.classesName.trim()

        if (classesName.isBlank()) {
            return@post call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Class name is required"
            )
        }

        if (classesName.length > 100) {
            return@post call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Class name cannot exceed 100 characters"
            )
        }

        val alreadyExists = ClassesRepository.existsByName(classesName)

        if (alreadyExists) {
            return@post call.respond(
                status = HttpStatusCode.Conflict,
                message = "A class with this name already exists"
            )
        }

        val id = ClassesRepository.create(
            request = request.copy(
                classesName = classesName
            )
        )

        val createdClass = ClassesRepository.getById(id)
            ?: return@post call.respond(
                status = HttpStatusCode.InternalServerError,
                message = "Class was created but could not be retrieved"
            )

        call.respond(
            status = HttpStatusCode.Created,
            message = createdClass
        )
    }

    put("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@put call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Invalid class ID"
            )

        val existingClass = ClassesRepository.getById(id)
            ?: return@put call.respond(
                status = HttpStatusCode.NotFound,
                message = "Class not found"
            )

        val request = call.receive<ClassesRequest>()

        val classesName = request.classesName.trim()

        if (classesName.isBlank()) {
            return@put call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Class name is required"
            )
        }

        if (classesName.length > 100) {
            return@put call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Class name cannot exceed 100 characters"
            )
        }

        val classWithSameName = ClassesRepository.getByName(classesName)

        if (
            classWithSameName != null &&
            classWithSameName.id != existingClass.id
        ) {
            return@put call.respond(
                status = HttpStatusCode.Conflict,
                message = "A class with this name already exists"
            )
        }

        val updated = ClassesRepository.update(
            id = id,
            request = request.copy(
                classesName = classesName
            )
        )

        if (!updated) {
            return@put call.respond(
                status = HttpStatusCode.NotFound,
                message = "Class not found"
            )
        }

        val updatedClass = ClassesRepository.getById(id)
            ?: return@put call.respond(
                status = HttpStatusCode.InternalServerError,
                message = "Class was updated but could not be retrieved"
            )

        call.respond(
            status = HttpStatusCode.OK,
            message = updatedClass
        )
    }

    delete("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Invalid class ID"
            )

        val deleted = ClassesRepository.delete(id)

        if (!deleted) {
            return@delete call.respond(
                status = HttpStatusCode.NotFound,
                message = "Class not found"
            )
        }

        call.respond(
            status = HttpStatusCode.OK,
            message = mapOf(
                "message" to "Class deleted successfully"
            )
        )
    }
}