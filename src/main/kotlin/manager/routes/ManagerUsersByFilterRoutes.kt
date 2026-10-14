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
        val requestStartedAt =
            System.currentTimeMillis()

        println(
            "=================================================="
        )

        println(
            "Manager filtered-users request started"
        )

        val principal =
            call.principal<AuthPrincipal>()

        if (principal == null) {
            println(
                "Manager filtered-users request rejected: " +
                        "authentication is required"
            )

            println(
                "Manager route total duration: ${
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

        val requestedFilter =
            call.request
                .queryParameters["dept"]
                ?.trim()
                .orEmpty()

        if (requestedFilter.isBlank()) {
            println(
                "Manager filtered-users request rejected: " +
                        "filter is required"
            )

            println(
                "Manager route total duration: ${
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

        val page =
            call.request
                .queryParameters["page"]
                ?.toIntOrNull()
                ?.coerceAtLeast(
                    minimumValue = 1
                )
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
            "Manager filter: $requestedFilter"
        )

        println(
            "Manager page: $page"
        )

        println(
            "Manager page size: $pageSize"
        )

        val transactionStartedAt =
            System.currentTimeMillis()

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
                                "/api/manager/users-per-department/manager"
                        )
                }
            } catch (
                exception: Exception
            ) {
                println(
                    "Manager filtered-users transaction failed"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
                )

                exception.printStackTrace()

                println(
                    "Manager transaction duration before failure: ${
                        System.currentTimeMillis() -
                                transactionStartedAt
                    } ms"
                )

                println(
                    "Manager route total failed duration: ${
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
            "Manager service and transaction duration: ${
                System.currentTimeMillis() -
                        transactionStartedAt
            } ms"
        )

        val responseStartedAt =
            System.currentTimeMillis()

        when (result) {
            is ManagerUsersByFilterResult.Success -> {
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
                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "This Manager account is inactive."
                    )
                )
            }

            ManagerUsersByFilterResult.ManagerRegionNotAssigned -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "detail" to
                                "A region has not been assigned to this Manager."
                    )
                )
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

        println(
            "Manager response duration: ${
                System.currentTimeMillis() -
                        responseStartedAt
            } ms"
        )

        println(
            "Manager route total duration: ${
                System.currentTimeMillis() -
                        requestStartedAt
            } ms"
        )

        println(
            "=================================================="
        )
    }
}