package com.hr.nextgrade



import com.hr.nextgrade.routes.nextGradeRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureNextGradeModule() {

    routing {
        route("/api") {
            nextGradeRoutes()
        }
    }
}