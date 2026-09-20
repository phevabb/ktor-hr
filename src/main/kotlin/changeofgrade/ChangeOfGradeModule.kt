package com.hr.changeofgrade

import com.hr.changeofgrade.routes.changeOfGradeRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.changeOfGradeModule() {

    routing {

        route("/api/change-of-grades") {

            changeOfGradeRoutes()
        }
    }
}
