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
        val principal =
            call.principal<AuthPrincipal>()
                ?: return@get call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf(
                        "detail" to
                                "Authentication is required."
                    )
                )

        val requestedFilter =
            call.request
                .queryParameters["dept"]
                ?.trim()
                .orEmpty()

        if (requestedFilter.isBlank()) {
            return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf(
                    "dept" to listOf(
                        "A filter value is required."
                    )
                )
            )
        }

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

        val result =
            try {
                dbQuery {
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
                }
            } catch (
                exception: Exception
            ) {
                exception.printStackTrace()

                return@get call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "Filtered users could not be retrieved."
                    )
                )
            }

        when (result) {
            is AdminUsersByFilterResult.Success -> {
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
                        "dept" to listOf(
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
    }
}