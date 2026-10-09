package com.hr.admin

import com.hr.admin.excel.route.adminUserExcelRoutes
import com.hr.admin.routes.adminDashboardRoutes
import com.hr.admin.routes.adminUserFieldsRoutes
import com.hr.admin.routes.adminUserUpdateRoutes
import com.hr.admin.services.CloudinaryAdminProfilePictureStorage
import com.hr.admin.routes.ageStatsRoutes
import com.hr.admin.routes.classStatsRoutes
import com.hr.admin.routes.contractStatsRoutes
import com.hr.admin.routes.departmentStatsRoutes
import com.hr.admin.routes.genderStatsRoutes
import com.hr.admin.routes.leaveTypeStatsRoutes
import com.hr.admin.routes.managementUnitStatsRoutes
import com.hr.admin.routes.professionalStatsRoutes
import com.hr.admin.routes.regionStatsRoutes
import com.hr.admin.routes.salaryGradeStatsRoutes
import com.hr.admin.routes.staffCategoryStatsRoutes
import com.hr.admin.routes.userFieldsRoutes
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.adminModule() {
    routing {
        route("/api/admin") {
            adminDashboardRoutes()
            professionalStatsRoutes()
            departmentStatsRoutes()
            classStatsRoutes()
            managementUnitStatsRoutes()
            regionStatsRoutes()
            staffCategoryStatsRoutes()
            genderStatsRoutes()
            leaveTypeStatsRoutes()
            contractStatsRoutes()
            salaryGradeStatsRoutes()
            ageStatsRoutes()
            userFieldsRoutes()
            authenticate(
                "auth-jwt"
            ) {
                adminUserUpdateRoutes(
                    profilePictureStorage =
                        CloudinaryAdminProfilePictureStorage
                )

                adminUserExcelRoutes()
            }






//            adminUserFieldsRoutes()
        }
    }

    println(
        "Admin module loaded: " +
                "GET /api/admin/dashboard-summary"
    )
}