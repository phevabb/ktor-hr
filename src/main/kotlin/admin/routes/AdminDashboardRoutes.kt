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
            val requestStartedAt =
                System.currentTimeMillis()

            println(
                "=================================================="
            )

            println(
                "Admin dashboard summary request started"
            )

            val authenticationStartedAt =
                System.currentTimeMillis()

            val principal =
                call.principal<AuthPrincipal>()

            println(
                "Dashboard principal lookup duration: ${
                    System.currentTimeMillis() -
                            authenticationStartedAt
                } ms"
            )

            if (principal == null) {
                val responseStartedAt =
                    System.currentTimeMillis()

                call.respond(
                    HttpStatusCode.Unauthorized,
                    AuthMessageResponse(
                        detail =
                            "Authentication is required."
                    )
                )

                println(
                    "Unauthorized response duration: ${
                        System.currentTimeMillis() -
                                responseStartedAt
                    } ms"
                )

                println(
                    "Dashboard total request duration: ${
                        System.currentTimeMillis() -
                                requestStartedAt
                    } ms"
                )

                println(
                    "=================================================="
                )

                return@get
            }

            println(
                "Dashboard account ID: ${principal.accountId}"
            )

            println(
                "Dashboard user ID: ${principal.userId}"
            )

            println(
                "Dashboard JWT role: ${principal.role}"
            )

            val transactionStartedAt =
                System.currentTimeMillis()

            val result =
                try {
                    dbQuery {
                        val serviceStartedAt =
                            System.currentTimeMillis()

                        println(
                            "Admin dashboard service started"
                        )

                        val serviceResult =
                            AdminDashboardService
                                .getDashboardSummary(
                                    accountId =
                                        principal.accountId
                                )

                        println(
                            "Admin dashboard service duration: ${
                                System.currentTimeMillis() -
                                        serviceStartedAt
                            } ms"
                        )

                        serviceResult
                    }
                } catch (
                    exception: Exception
                ) {
                    println(
                        "Admin dashboard transaction failed"
                    )

                    println(
                        "Dashboard error type: " +
                                "${exception::class.simpleName}"
                    )

                    println(
                        "Dashboard error message: " +
                                "${exception.message}"
                    )

                    exception.printStackTrace()

                    println(
                        "Failed dashboard transaction duration: ${
                            System.currentTimeMillis() -
                                    transactionStartedAt
                        } ms"
                    )

                    val responseStartedAt =
                        System.currentTimeMillis()

                    call.respond(
                        HttpStatusCode.InternalServerError,
                        AuthMessageResponse(
                            detail =
                                "The admin dashboard summary could not be retrieved."
                        )
                    )

                    println(
                        "Failed dashboard response duration: ${
                            System.currentTimeMillis() -
                                    responseStartedAt
                        } ms"
                    )

                    println(
                        "Dashboard total failed request duration: ${
                            System.currentTimeMillis() -
                                    requestStartedAt
                        } ms"
                    )

                    println(
                        "=================================================="
                    )

                    return@get
                }

            println(
                "Admin dashboard transaction duration: ${
                    System.currentTimeMillis() -
                            transactionStartedAt
                } ms"
            )

            val responseStartedAt =
                System.currentTimeMillis()

            when (result) {
                is AdminDashboardResult.Success -> {
                    println(
                        "Dashboard users: ${result.summary.numOfUsers}"
                    )

                    println(
                        "Dashboard females: ${result.summary.numOfFemales}"
                    )

                    println(
                        "Dashboard males: ${result.summary.numOfMales}"
                    )

                    println(
                        "Dashboard staff: ${result.summary.numOfStaffs}"
                    )

                    println(
                        "Dashboard admins: ${result.summary.numOfAdmins}"
                    )

                    println(
                        "Dashboard managers: ${result.summary.numOfManagers}"
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

            println(
                "Dashboard response serialization duration: ${
                    System.currentTimeMillis() -
                            responseStartedAt
                } ms"
            )

            println(
                "Dashboard total request duration: ${
                    System.currentTimeMillis() -
                            requestStartedAt
                } ms"
            )

            println(
                "=================================================="
            )
        }
    }
}