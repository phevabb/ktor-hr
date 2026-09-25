package com.hr.admin

import com.hr.admin.routes.adminDashboardRoutes
import com.hr.admin.routes.classStatsRoutes
import com.hr.admin.routes.departmentStatsRoutes
import com.hr.admin.routes.professionalStatsRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.adminModule() {
    routing {
        route("/api/admin") {
            adminDashboardRoutes()
            professionalStatsRoutes()
            departmentStatsRoutes()
            classStatsRoutes()
        }
    }

    println(
        "Admin module loaded: " +
                "GET /api/admin/dashboard-summary"
    )
}