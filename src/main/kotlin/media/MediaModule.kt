package com.hr.media



import com.hr.cloudinary.CloudinaryClient
import com.hr.media.routes.mediaRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.mediaModule() {
    /*
     * Initialize Cloudinary when the media module loads.
     *
     * This also checks whether the required Cloudinary
     * environment variables are available.
     */
    CloudinaryClient.instance

    routing {
        route("/api/media") {
            mediaRoutes()
        }
    }

    println(
        "Media module loaded: /api/media"
    )
}