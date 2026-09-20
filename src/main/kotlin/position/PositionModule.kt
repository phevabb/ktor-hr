package com.hr.position

import com.hr.position.routes.positionRoutes




import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.positionModule() {

    routing {
        route("/api/positions") {
            positionRoutes()
        }
    }
}
