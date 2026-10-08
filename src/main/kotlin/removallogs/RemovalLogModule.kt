package com.hr.removallogs

import com.hr.removallogs.route.removalLogRoutes
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing

fun Application.configureRemovalLogs() {
    println(
        "Configuring removal-log module"
    )

    routing {
        authenticate(
            "auth-jwt"
        ) {
            removalLogRoutes()
        }
    }

    println(
        "Removal-log module configured successfully"
    )
}