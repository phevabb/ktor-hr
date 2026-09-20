package com.hr.region.routes



import com.hr.region.dtos.RegionRequest
import com.hr.region.repos.RegionRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.regionRoutes() {

    get {
        call.respond(
            RegionRepository.getAll()
        )
    }

    get("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Invalid region ID"
            )

        val region = RegionRepository.getById(id)
            ?: return@get call.respond(
                HttpStatusCode.NotFound,
                "Region not found"
            )

        call.respond(
            HttpStatusCode.OK,
            region
        )
    }

    post {
        println("POST /api/regions received")
        println("Content-Type: ${call.request.contentType()}")

        val request = try {
            call.receive<RegionRequest>()
        } catch (e: Exception) {
            println("Failed to receive RegionRequest")
            println("Error type: ${e::class.qualifiedName}")
            println("Error message: ${e.message}")
            e.printStackTrace()

            return@post call.respond(
                HttpStatusCode.BadRequest,
                "Invalid request body: ${e.message}"
            )
        }

        println("Region request received: $request")
        println("Region name received: ${request.region}")

        val id = RegionRepository.create(request)

        println("Region created with ID: $id")

        val region = RegionRepository.getById(id)

        println("Region retrieved from database: $region")

        if (region == null) {
            println("Region with ID $id was created but could not be retrieved")

            return@post call.respond(
                HttpStatusCode.InternalServerError,
                "Region was created but could not be retrieved"
            )
        }

        println("Returning created region: $region")

        call.respond(
            HttpStatusCode.Created,
            region
        )
    }

    put("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                "Invalid region ID"
            )

        val request = call.receive<RegionRequest>()

        val updated = RegionRepository.update(
            id = id,
            request = request
        )

        if (updated) {
            val region = RegionRepository.getById(id)

            if (region != null) {
                call.respond(
                    HttpStatusCode.OK,
                    region
                )
            } else {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Region was updated but could not be retrieved"
                )
            }
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                "Region not found"
            )
        }
    }

    delete("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                "Invalid region ID"
            )

        val deleted = RegionRepository.delete(id)

        if (deleted) {
            call.respond(
                HttpStatusCode.OK,
                "Region deleted successfully"
            )
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                "Region not found"
            )
        }
    }
}