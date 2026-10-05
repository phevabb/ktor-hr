package com.hr.manager.routes

import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.manager.repositories.ManagerUsersByFilterResult

import com.hr.manager.services.ManagerUsersByFilterService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.managerUsersByFilterRoutes() {
    get("/users-per-department/manager") {
        println(
            "=================================================="
        )

        println(
            "GET /api/manager/users-per-department request received"
        )

        val principal =
            call.principal<AuthPrincipal>()
                ?: return@get call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf(
                        "detail" to
                                "Authentication is required."
                    )
                )

        println(
            "Authenticated account ID: ${principal.accountId}"
        )

        println(
            "Authenticated user ID: ${principal.userId}"
        )

        println(
            "Authenticated role: ${principal.role}"
        )

        val requestedFilter =
            call.request
                .queryParameters["dept"]
                ?.trim()
                ?: ""

        val page =
            call.request
                .queryParameters["page"]
                ?.toIntOrNull()
                ?.coerceAtLeast(
                    1
                )
                ?: 1

        val pageSize =
            call.request
                .queryParameters["page_size"]
                ?.toIntOrNull()
                ?.coerceIn(
                    minimumValue =
                        1,

                    maximumValue =
                        100
                )
                ?: 10

        println(
            "Requested filter: $requestedFilter"
        )

        println(
            "Requested page: $page"
        )

        println(
            "Requested page size: $pageSize"
        )

        val result =
            try {
                dbQuery {
                    ManagerUsersByFilterService
                        .getUsersByFilter(
                            managerAccountId =
                                principal.accountId,

                            requestedFilter =
                                requestedFilter,

                            page =
                                page,

                            pageSize =
                                pageSize,

                            requestPath =
                                "/api/manager/users-per-department"
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Unable to process Manager users-by-filter request"
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
                                "Filtered users could not be retrieved."
                    )
                )
            }

        when (result) {
            is ManagerUsersByFilterResult.Success -> {
                println(
                    "Manager filtered users returned successfully"
                )



                call.respond(
                    HttpStatusCode.OK,
                    result.response
                )
            }

            ManagerUsersByFilterResult.AccessDenied -> {
                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "Manager access is required."
                    )
                )
            }

            ManagerUsersByFilterResult.ManagerAccountNotFound -> {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf(
                        "detail" to
                                "The authenticated Manager account was not found."
                    )
                )
            }

            ManagerUsersByFilterResult.ManagerAccountInactive -> {
            }

            ManagerUsersByFilterResult.ManagerRegionNotAssigned -> {
            }

            ManagerUsersByFilterResult.FilterRequired -> {
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

            ManagerUsersByFilterResult.Failed -> {
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