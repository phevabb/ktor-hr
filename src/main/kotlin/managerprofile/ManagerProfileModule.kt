package com.hr.managerprofile

import com.hr.managerprofile.routes.managerProfileRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.managerProfileModule() {
    routing {
        route("/api/manager-profiles") {
            managerProfileRoutes()
        }
    }

    println(
        "Manager profile module loaded: " +
                "/api/manager-profiles"
    )
}