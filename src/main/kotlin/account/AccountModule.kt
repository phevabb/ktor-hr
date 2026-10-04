package com.hr.account

import com.hr.account.routes.accountRoutes
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.accountModule() {
    routing {
        authenticate("auth-jwt") {
            route("/api/accounts") {
                accountRoutes()
            }
        }
    }

    println(
        "Account module loaded"
    )

    println(
        "GET /api/accounts"
    )

    println(
        "GET /api/accounts/{id}"
    )

    println(
        "POST /api/accounts"
    )

    println(
        "PUT /api/accounts/{id}"
    )

    println(
        "DELETE /api/accounts/{id}"
    )
}