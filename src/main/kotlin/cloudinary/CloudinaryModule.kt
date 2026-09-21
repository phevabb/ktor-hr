package com.hr.cloudinary



import com.hr.cloudinary.routes.cloudinaryRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.cloudinaryModule() {

    /*
     * Initialize the client during startup.
     *
     * This also verifies that the required environment
     * variables are available.
     */
    CloudinaryClient.instance

    routing {
        route("/api/media") {
            cloudinaryRoutes()
        }
    }

    println(
        "Cloudinary module loaded: /api/media"
    )
}