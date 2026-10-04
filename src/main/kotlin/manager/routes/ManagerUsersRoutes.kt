package com.hr.manager.routes

import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.manager.services.ManagerUsersResult
import com.hr.manager.services.ManagerUsersService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.managerUsersRoutes() {
    get("/users") {
        println(
            "=================================================="
        )

        println(
            "GET /api/manager/users request received"
        )

        val principal =
            call.principal<AuthPrincipal>()

        if (principal == null) {
            println(
                "Manager users request has no authenticated principal"
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

        val page =
            call.request
                .queryParameters["page"]
                ?.toIntOrNull()
                ?.coerceAtLeast(1)
                ?: 1

        val pageSize =
            call.request
                .queryParameters[
                "page_size"
            ]
                ?.toIntOrNull()
                ?.coerceIn(
                    minimumValue = 1,
                    maximumValue = 100
                )
                ?: 10

        println(
            "Requested page: $page"
        )

        println(
            "Requested page size: $pageSize"
        )

        val result =
            try {
                dbQuery {
                    ManagerUsersService
                        .getUsersInManagerRegion(
                            accountId =
                                principal.accountId,

                            page =
                                page,

                            pageSize =
                                pageSize,

                            requestPath =
                                "/api/manager/users"
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Unable to process Manager users request"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
                )

                return@get call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "Users in the Manager's region could not be retrieved."
                    )
                )
            }

        when (result) {
            is ManagerUsersResult.Success -> {
                println(
                    "Manager users request completed successfully"
                )

                println(
                    "Total records: ${result.response.count}"
                )

                println(
                    "Page result count: ${result.response.results.size}"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.OK,
                    result.response
                )
            }

            ManagerUsersResult.AccessDenied -> {
                println(
                    "Manager users access denied"
                )

                println(
                    "The authenticated database account is not a Manager"
                )

                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "Manager access is required."
                    )
                )
            }

            ManagerUsersResult.AccountNotFound -> {
                println(
                    "Authenticated account was not found"
                )

                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf(
                        "detail" to
                                "The authenticated account was not found."
                    )
                )
            }

            ManagerUsersResult.AccountInactive -> {
                println(
                    "Inactive Manager attempted to retrieve region users"
                )

                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "This Manager account is inactive."
                    )
                )
            }

            ManagerUsersResult.RegionNotAssigned -> {
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

            ManagerUsersResult.Failed -> {
                println(
                    "Manager users service returned Failed"
                )

                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "Users in the Manager's region could not be retrieved."
                    )
                )
            }
        }
    }
}