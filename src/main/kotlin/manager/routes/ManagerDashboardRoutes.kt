package com.hr.manager.routes

import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.manager.services.ManagerDashboardResult
import com.hr.manager.services.ManagerDashboardService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.managerDashboardRoutes() {
    get("/admin-dashboard-summary") {
        println(
            "=================================================="
        )

        println(
            "GET /api/manager/admin-dashboard-summary request received"
        )

        val principal =
            call.principal<AuthPrincipal>()

        if (principal == null) {
            println(
                "Manager dashboard request has no authenticated principal"
            )

            return@get call.respond(
                HttpStatusCode.Unauthorized,
                mapOf(
                    "detail" to
                            "Authentication is required."
                )
            )
        }

        println(
            "Authenticated account ID: ${principal.accountId}"
        )

        println(
            "Authenticated user ID: ${principal.userId}"
        )

        println(
            "JWT role: ${principal.role}"
        )

        val result =
            try {
                dbQuery {
                    ManagerDashboardService
                        .getDashboardSummary(
                            managerAccountId =
                                principal.accountId
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Unable to process Manager dashboard summary request"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
                )

                println(
                    "=================================================="
                )

                return@get call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "The Manager dashboard summary could not be retrieved."
                    )
                )
            }

        when (result) {
            is ManagerDashboardResult.Success -> {
                println(
                    "Manager dashboard summary returned successfully"
                )

                println(
                    "Active users: ${result.summary.numOfUsers}"
                )

                println(
                    "Females: ${result.summary.numOfFemales}"
                )

                println(
                    "Males: ${result.summary.numOfMales}"
                )

                println(
                    "Staff: ${result.summary.numOfStaffs}"
                )

                println(
                    "Admins: ${result.summary.numOfAdmins}"
                )

                println(
                    "Managers: ${result.summary.numOfManagers}"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.OK,
                    result.summary
                )
            }

            ManagerDashboardResult.AccessDenied -> {
                println(
                    "Manager dashboard access denied"
                )

                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "Manager access is required."
                    )
                )
            }

            ManagerDashboardResult.AccountNotFound -> {
                println(
                    "Authenticated Manager account was not found"
                )

                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf(
                        "detail" to
                                "The authenticated Manager account was not found."
                    )
                )
            }

            ManagerDashboardResult.AccountInactive -> {
                println(
                    "Inactive Manager attempted to access the dashboard summary"
                )

                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "This Manager account is inactive."
                    )
                )
            }

            ManagerDashboardResult.RegionNotAssigned -> {
                println(
                    "Manager does not have an assigned region"
                )

                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "detail" to
                                "A region has not been assigned to this Manager account."
                    )
                )
            }

            ManagerDashboardResult.Failed -> {
                println(
                    "Manager dashboard summary service returned Failed"
                )

                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "The Manager dashboard summary could not be retrieved."
                    )
                )
            }
        }
    }
}