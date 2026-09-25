package com.hr.admin.routes

import com.hr.admin.services.AdminDashboardResult
import com.hr.admin.services.AdminDashboardService
import com.hr.auth.dtos.AuthMessageResponse
import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.adminDashboardRoutes() {
    authenticate("auth-jwt") {
        get("/dashboard-summary") {
            val principal =
                call.principal<AuthPrincipal>()
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        AuthMessageResponse(
                            detail =
                                "Authentication is required."
                        )
                    )

            println(
                "Admin dashboard summary requested: " +
                        "accountId=${principal.accountId}, " +
                        "userId=${principal.userId}, " +
                        "role=${principal.role}"
            )

            val result =
                try {
                    dbQuery {
                        AdminDashboardService
                            .getDashboardSummary(
                                accountId =
                                    principal.accountId
                            )
                    }
                } catch (exception: Exception) {
                    println(
                        "Unable to retrieve admin dashboard summary: " +
                                "${exception::class.simpleName}: " +
                                "${exception.message}"
                    )

                    return@get call.respond(
                        HttpStatusCode.InternalServerError,
                        AuthMessageResponse(
                            detail =
                                "The admin dashboard summary could not be retrieved."
                        )
                    )
                }

            when (result) {
                is AdminDashboardResult.Success -> {
                    println(
                        "Admin dashboard summary returned: " +
                                "users=${result.summary.numOfUsers}, " +
                                "females=${result.summary.numOfFemales}, " +
                                "males=${result.summary.numOfMales}, " +
                                "staff=${result.summary.numOfStaffs}, " +
                                "admins=${result.summary.numOfAdmins}, " +
                                "managers=${result.summary.numOfManagers}"
                    )

                    call.respond(
                        HttpStatusCode.OK,
                        result.summary
                    )
                }

                AdminDashboardResult.AccessDenied -> {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        AuthMessageResponse(
                            detail =
                                "Admin access is required."
                        )
                    )
                }

                AdminDashboardResult.AccountNotFound -> {
                    call.respond(
                        HttpStatusCode.NotFound,
                        AuthMessageResponse(
                            detail =
                                "The authenticated account was not found."
                        )
                    )
                }

                AdminDashboardResult.AccountInactive -> {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        AuthMessageResponse(
                            detail =
                                "This account is inactive."
                        )
                    )
                }

                AdminDashboardResult.Failed -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        AuthMessageResponse(
                            detail =
                                "The admin dashboard summary could not be retrieved."
                        )
                    )
                }
            }
        }
    }
}
