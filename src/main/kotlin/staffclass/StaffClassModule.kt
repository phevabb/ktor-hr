package com.hr.staffclass




import com.hr.staffclass.routes.staffClassRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.staffClassModule() {

    routing {
        route("/api/staff-classes") {
            staffClassRoutes()
        }
    }
}