package com.hr.title

import com.hr.title.routes.titleRoutes




import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.titleModule() {

    routing {
        route("/api/titles") {
            titleRoutes()
        }
    }
}