package com.hr.onleavetype


import com.hr.onleavetype.routes.onLeaveTypeRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.onLeaveTypeModule() {

    routing {
        route("/api/on-leave-types") {
            onLeaveTypeRoutes()
        }
    }
}