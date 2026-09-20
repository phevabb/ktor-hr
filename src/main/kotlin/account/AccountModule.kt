package com.hr.account

import com.hr.account.routes.accountRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.accountModule() {

    routing {
        route("/api/accounts") {
            accountRoutes()
        }
    }
}
