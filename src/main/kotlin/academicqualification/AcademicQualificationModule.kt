package com.hr.academicqualification


import com.hr.academicqualification.routes.academicQualificationRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.academicQualificationModule() {

    routing {
        route("/api/academic-qualifications") {
            academicQualificationRoutes()
        }
    }
}