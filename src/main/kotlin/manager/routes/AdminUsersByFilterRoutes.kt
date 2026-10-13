package com.hr.manager.routes

import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.manager.services.AdminUsersByFilterResult
import com.hr.manager.services.AdminUsersByFilterService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.adminUsersByFilterRoutes() {

    get("/users-per-department/admin") {
        val requestStartedAt =
            System.currentTimeMillis()

        println(
            "=================================================="
        )

        println(
            "Admin filtered-users request started"
        )

        val authenticationStartedAt =
            System.currentTimeMillis()

        val principal =
            call.principal<AuthPrincipal>()

        println(
            "Authentication lookup duration: ${
                System.currentTimeMillis() -
                        authenticationStartedAt
            } ms"
        )

        if (principal == null) {
            println(
                "Request rejected: Authentication is required"
            )

            println(
                "Total request duration: ${
                    System.currentTimeMillis() -
                            requestStartedAt
                } ms"
            )

            println(
                "=================================================="
            )

            return@get call.respond(
                HttpStatusCode.Unauthorized,
                mapOf(
                    "detail" to
                            "Authentication is required."
                )
            )
        }

        val parameterStartedAt =
            System.currentTimeMillis()

        val requestedFilter =
            call.request
                .queryParameters["dept"]
                ?.trim()
                .orEmpty()

        val page =
            call.request
                .queryParameters["page"]
                ?.toIntOrNull()
                ?.coerceAtLeast(1)
                ?: 1

        val pageSize =
            call.request
                .queryParameters["page_size"]
                ?.toIntOrNull()
                ?.coerceIn(
                    minimumValue = 1,
                    maximumValue = 100
                )
                ?: 10

        println(
            "Parameter parsing duration: ${
                System.currentTimeMillis() -
                        parameterStartedAt
            } ms"
        )

        println(
            "Filter: $requestedFilter"
        )

        println(
            "Page: $page"
        )

        println(
            "Page size: $pageSize"
        )

        if (requestedFilter.isBlank()) {
            println(
                "Request rejected: Filter is required"
            )

            println(
                "Total request duration: ${
                    System.currentTimeMillis() -
                            requestStartedAt
                } ms"
            )

            println(
                "=================================================="
            )

            return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf(
                    "dept" to
                            listOf(
                                "A filter value is required."
                            )
                )
            )
        }

        val transactionStartedAt =
            System.currentTimeMillis()

        val result =
            try {
                dbQuery {
                    val serviceStartedAt =
                        System.currentTimeMillis()

                    val serviceResult =
                        AdminUsersByFilterService
                            .getUsersByFilter(
                                adminAccountId =
                                    principal.accountId,

                                requestedFilter =
                                    requestedFilter,

                                page =
                                    page,

                                pageSize =
                                    pageSize,

                                requestPath =
                                    "/api/superadmin/users-per-department/admin"
                            )

                    println(
                        "Service duration inside transaction: ${
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
                    "Filtered-users transaction failed"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
                )

                exception.printStackTrace()

                println(
                    "Failed transaction duration: ${
                        System.currentTimeMillis() -
                                transactionStartedAt
                    } ms"
                )

                println(
                    "Total failed request duration: ${
                        System.currentTimeMillis() -
                                requestStartedAt
                    } ms"
                )

                println(
                    "=================================================="
                )

                return@get call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "Filtered users could not be retrieved."
                    )
                )
            }

        println(
            "Database transaction duration: ${
                System.currentTimeMillis() -
                        transactionStartedAt
            } ms"
        )

        val responseStartedAt =
            System.currentTimeMillis()

        when (result) {
            is AdminUsersByFilterResult.Success -> {
                println(
                    "Matching users: ${result.response.count}"
                )

                println(
                    "Users returned on page: ${
                        result.response.results.users.size
                    }"
                )

                call.respond(
                    HttpStatusCode.OK,
                    result.response
                )
            }

            AdminUsersByFilterResult.AccountNotFound -> {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf(
                        "detail" to
                                "The authenticated Admin account was not found."
                    )
                )
            }

            AdminUsersByFilterResult.AccountInactive -> {
                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "This Admin account is inactive."
                    )
                )
            }

            AdminUsersByFilterResult.AccessDenied -> {
                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "Admin access is required."
                    )
                )
            }

            AdminUsersByFilterResult.FilterRequired -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "dept" to
                                listOf(
                                    "A filter value is required."
                                )
                    )
                )
            }

            AdminUsersByFilterResult.Failed -> {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "Filtered users could not be retrieved."
                    )
                )
            }
        }

        println(
            "Response building and serialization duration: ${
                System.currentTimeMillis() -
                        responseStartedAt
            } ms"
        )

        println(
            "Total request duration: ${
                System.currentTimeMillis() -
                        requestStartedAt
            } ms"
        )

        println(
            "=================================================="
        )
    }
}