package com.hr.currentgrade

import com.hr.currentgrade.routes.currentGradeRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.currentGradeModule() {

    routing {

        route("/api/current-grades") {

            currentGradeRoutes()
        }
    }
}