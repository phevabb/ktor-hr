package com.hr.department.route


import com.hr.department.dto.DepartmentRequest
import com.hr.department.repo.DepartmentRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.departmentRoutes() {

    get {

        call.respond(
            HttpStatusCode.OK,
            DepartmentRepository.getAll()
        )
    }

    get("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Invalid department ID"
            )

        val department = DepartmentRepository.getById(id)
            ?: return@get call.respond(
                HttpStatusCode.NotFound,
                "Department not found"
            )

        call.respond(
            HttpStatusCode.OK,
            department
        )
    }

    post {

        val request = call.receive<DepartmentRequest>()

        val id = DepartmentRepository.create(request)

        val department = DepartmentRepository.getById(id)

        call.respond(
            HttpStatusCode.Created,
            department!!
        )
    }

    put("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                "Invalid department ID"
            )

        val request = call.receive<DepartmentRequest>()

        val updated = DepartmentRepository.update(
            id = id,
            request = request
        )

        if (!updated) {
            return@put call.respond(
                HttpStatusCode.NotFound,
                "Department not found"
            )
        }

        val department = DepartmentRepository.getById(id)

        call.respond(
            HttpStatusCode.OK,
            department!!
        )
    }

    delete("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                "Invalid department ID"
            )

        val deleted = DepartmentRepository.delete(id)

        if (!deleted) {
            return@delete call.respond(
                HttpStatusCode.NotFound,
                "Department not found"
            )
        }

        call.respond(
            HttpStatusCode.OK,
            mapOf(
                "message" to "Department deleted successfully"
            )
        )
    }
}