package com.hr.auth



import com.hr.auth.config.JwtConfig
import com.hr.auth.routes.authRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.authModule() {
    /*
     * Initialize JWT configuration during startup.
     * This checks that JWT_SECRET is available.
     */
    JwtConfig.verifier

    routing {
        route("/api/auth") {
            authRoutes()
        }
    }

    println(
        "Authentication module loaded: /api/auth"
    )
}