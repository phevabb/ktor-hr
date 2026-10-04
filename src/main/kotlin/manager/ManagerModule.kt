package com.hr.manager

import com.hr.manager.routes.managerClassStatsRoutes
import com.hr.manager.routes.managerContractStatsRoutes
import com.hr.manager.routes.managerDashboardRoutes
import com.hr.manager.routes.managerDirectorateStatsRoutes
import com.hr.manager.routes.managerGenderStatsRoutes
import com.hr.manager.routes.managerLeaveTypeStatsRoutes
import com.hr.manager.routes.managerManagementStatsRoutes
import com.hr.manager.routes.managerProfessionalStatsRoutes
import com.hr.manager.routes.managerSalaryGradeStatsRoutes
import com.hr.manager.routes.managerStaffCategoryStatsRoutes
import com.hr.manager.routes.managerUserDetailsRoutes
import com.hr.manager.routes.managerUsersRoutes
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.managerModule() {
    routing {
        authenticate("auth-jwt") {
            route("/api/manager") {
                managerClassStatsRoutes()
                managerDashboardRoutes()
                managerUsersRoutes()
                managerUserDetailsRoutes()
                managerContractStatsRoutes()
                managerProfessionalStatsRoutes()
                managerManagementStatsRoutes()
                managerDirectorateStatsRoutes()
                managerLeaveTypeStatsRoutes()
                managerSalaryGradeStatsRoutes()
                managerStaffCategoryStatsRoutes()
                managerGenderStatsRoutes()

            }
        }
    }

    println(
        "Manager module loaded"
    )

    println(
        "GET /api/manager/users"
    )
}