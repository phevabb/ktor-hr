package com.hr.classes

import com.hr.classes.routes.classesRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.classesModule() {

    routing {

        route("/api/classes") {

            classesRoutes()
        }
    }
}