package com.hr.department



import com.hr.department.route.departmentRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.departmentModule() {

    routing {

        route("/api/departments") {

            departmentRoutes()
        }
    }
}