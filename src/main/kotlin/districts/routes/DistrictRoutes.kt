package com.hr.districts.routes


import com.hr.districts.dtos.DistrictRequest
import com.hr.districts.repos.DistrictRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.districtRoutes() {

    get {

        call.respond(
            HttpStatusCode.OK,
            DistrictRepository.getAll()
        )
    }

    get("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Invalid district ID"
            )

        val district = DistrictRepository.getById(id)
            ?: return@get call.respond(
                HttpStatusCode.NotFound,
                "District not found"
            )

        call.respond(
            HttpStatusCode.OK,
            district
        )
    }

    post {

        val request = call.receive<DistrictRequest>()

        val id = DistrictRepository.create(request)

        val district = DistrictRepository.getById(id)

        call.respond(
            HttpStatusCode.Created,
            district!!
        )
    }

    put("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                "Invalid district ID"
            )

        val request = call.receive<DistrictRequest>()

        val updated = DistrictRepository.update(
            id = id,
            request = request
        )

        if (!updated) {
            return@put call.respond(
                HttpStatusCode.NotFound,
                "District not found"
            )
        }

        val district = DistrictRepository.getById(id)

        call.respond(
            HttpStatusCode.OK,
            district!!
        )
    }

    delete("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                "Invalid district ID"
            )

        val deleted = DistrictRepository.delete(id)

        if (!deleted) {
            return@delete call.respond(
                HttpStatusCode.NotFound,
                "District not found"
            )
        }

        call.respond(
            HttpStatusCode.OK,
            mapOf(
                "message" to "District deleted successfully"
            )
        )
    }
}