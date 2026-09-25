package com.hr.staff

import com.hr.staff.routes.staffRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.staffModule() {
    routing {
        route("/api/staff") {
            staffRoutes()
        }
    }

    println(
        "Staff module loaded: /api/staff"
    )
}