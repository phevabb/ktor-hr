package com.hr.region




import com.hr.region.routes.regionRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.regionModule() {

    routing {

        route("/api") {

            route("/regions") {
                regionRoutes()
            }

        }
    }
}