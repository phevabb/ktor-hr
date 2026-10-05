package com.hr.manager.routes

import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.manager.services.ManagerUsersByFilterNoPagesResult
import com.hr.manager.services.ManagerUsersByFilterNoPagesService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.managerUsersByFilterNoPagesRoutes() {
    get("/users-per-department-no-pages") {
        println(
            "=================================================="
        )

        println(
            "GET /api/manager/users-per-department-no-pages request received"
        )

        val principal =
            call.principal<AuthPrincipal>()
                ?: run {
                    println(
                        "Manager non-paginated users-by-filter request rejected"
                    )

                    println(
                        "Reason: Authentication is required"
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

        println(
            "Requested filter: $requestedFilter"
        )

        val result =
            try {
                dbQuery {
                    ManagerUsersByFilterNoPagesService
                        .getUsersByFilter(
                            managerAccountId =
                                principal.accountId,

                            requestedFilter =
                                requestedFilter
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Unable to process Manager non-paginated users-by-filter request"
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
            is ManagerUsersByFilterNoPagesResult.Success -> {
                println(
                    "Manager non-paginated filtered users returned successfully"
                )

                println(
                    "Filter: ${result.response.dept}"
                )

                println(
                    "Filter type: ${result.response.filterType}"
                )

                println(
                    "Total matching users: ${result.response.count}"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.OK,
                    result.response
                )
            }

            ManagerUsersByFilterNoPagesResult.AccessDenied -> {
                println(
                    "Manager non-paginated users-by-filter access denied"
                )

                println(
                    "Account ID: ${principal.accountId}"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "Manager access is required."
                    )
                )
            }

            ManagerUsersByFilterNoPagesResult.ManagerAccountNotFound -> {
                println(
                    "Authenticated Manager account was not found"
                )

                println(
                    "Account ID: ${principal.accountId}"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf(
                        "detail" to
                                "The authenticated Manager account was not found."
                    )
                )
            }

            ManagerUsersByFilterNoPagesResult.ManagerAccountInactive -> {
                println(
                    "Inactive Manager attempted to retrieve filtered users"
                )

                println(
                    "Account ID: ${principal.accountId}"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "This Manager account is inactive."
                    )
                )
            }

            ManagerUsersByFilterNoPagesResult.ManagerRegionNotAssigned -> {
                println(
                    "Manager does not have an assigned region"
                )

                println(
                    "Account ID: ${principal.accountId}"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "detail" to
                                "A region has not been assigned to this Manager account."
                    )
                )
            }

            ManagerUsersByFilterNoPagesResult.FilterRequired -> {
                println(
                    "Manager non-paginated users-by-filter request has no filter"
                )

                println(
                    "=================================================="
                )

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

            ManagerUsersByFilterNoPagesResult.Failed -> {
                println(
                    "Manager non-paginated users-by-filter service failed"
                )

                println(
                    "Account ID: ${principal.accountId}"
                )

                println(
                    "=================================================="
                )

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