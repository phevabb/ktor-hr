package com.hr.districts




import com.hr.districts.routes.districtRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.districtModule() {

    routing {

        route("/api/districts") {

            districtRoutes()
        }
    }
}