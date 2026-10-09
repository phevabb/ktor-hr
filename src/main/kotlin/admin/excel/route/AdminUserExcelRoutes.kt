package com.hr.admin.excel.route

import com.hr.admin.excel.repository.AdminUserExcelRepository
import com.hr.config.DatabaseFactory.dbQuery
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable
private data class AdminExcelExportErrorResponse(
    val error: String
)

fun Route.adminUserExcelRoutes() {
    route(
        "/all-users-to-excel"
    ) {
        get(

        ) {
            println(
                "=================================================="
            )

            println(
                "GET /api/admin/api/v1/all-users-to-excel request received"
            )

            val requestStartedAt =
                System.currentTimeMillis()

            val users =
                try {
                    dbQuery {
                        AdminUserExcelRepository
                            .getAllActiveUsers()
                    }
                } catch (exception: Exception) {
                    println(
                        "Unable to prepare Admin user Excel export"
                    )

                    println(
                        "Error type: ${exception::class.simpleName}"
                    )

                    println(
                        "Error message: ${exception.message}"
                    )

                    exception.cause?.let { cause ->
                        println(
                            "Cause type: ${cause::class.simpleName}"
                        )

                        println(
                            "Cause message: ${cause.message}"
                        )
                    }

                    exception.printStackTrace()

                    println(
                        "GET /api/admin/api/v1/all-users-to-excel completed with HTTP 500"
                    )

                    println(
                        "=================================================="
                    )

                    return@get call.respond(
                        HttpStatusCode.InternalServerError,
                        AdminExcelExportErrorResponse(
                            error =
                                "The active user export could not be prepared."
                        )
                    )
                }

            val requestDuration =
                System.currentTimeMillis() -
                        requestStartedAt

            println(
                "Admin user Excel export prepared successfully"
            )

            println(
                "Active users returned: ${users.size}"
            )

            println(
                "Request duration: $requestDuration ms"
            )

            println(
                "GET /api/admin/api/v1/all-users-to-excel completed with HTTP 200"
            )

            println(
                "=================================================="
            )

            call.respond(
                HttpStatusCode.OK,
                users
            )
        }
    }
}