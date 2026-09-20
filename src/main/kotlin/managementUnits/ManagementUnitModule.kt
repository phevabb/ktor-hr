package com.hr.managementUnits


import com.hr.managementUnits.routes.managementUnitRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.managementUnitModule() {

    routing {

        route("/api/management-units") {

            managementUnitRoutes()
        }
    }
}