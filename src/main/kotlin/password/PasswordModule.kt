package com.hr.password

import com.hr.password.route.passwordRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.passwordModule() {
    routing {
        route(
            "/api"
        ) {
            passwordRoutes()
        }
    }

    println(
        "Password module loaded"
    )

    println(
        "POST /api/password/forgot"
    )

    println(
        "POST /api/password/reset"
    )

    println(
        "POST /api/password/change"
    )
}