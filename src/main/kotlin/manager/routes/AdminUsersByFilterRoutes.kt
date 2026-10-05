package com.hr.superadmin.routes

import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.superadmin.services.AdminUsersByFilterResult
import com.hr.superadmin.services.AdminUsersByFilterService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.adminUsersByFilterRoutes() {
    get("/users-per-department/admin") {
        println(
            "=================================================="
        )

        println(
            "GET /api/superadmin/users-per-department request received"
        )

        val principal =
            call.principal<AuthPrincipal>()
                ?: run {
                    println(
                        "Admin users-by-filter request rejected"
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
            "Authenticated JWT role: ${principal.role}"
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
                                "/api/superadmin/users-per-department"
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Unable to process Admin users-by-filter request"
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
            is AdminUsersByFilterResult.Success -> {
                println(
                    "Admin paginated filtered users returned successfully"
                )

                println(
                    "Filter: ${result.response.results.dept}"
                )

                println(
                    "Filter type: ${result.response.results.filterType}"
                )

                println(
                    "Total matching users: ${result.response.count}"
                )

                println(
                    "Users returned: ${result.response.results.users.size}"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.OK,
                    result.response
                )
            }

            AdminUsersByFilterResult.AccountNotFound -> {
                println(
                    "Authenticated Admin account was not found"
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
                                "The authenticated Admin account was not found."
                    )
                )
            }

            AdminUsersByFilterResult.AccountInactive -> {
                println(
                    "Inactive Admin attempted to retrieve filtered users"
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
                                "This Admin account is inactive."
                    )
                )
            }

            AdminUsersByFilterResult.AccessDenied -> {
                println(
                    "Admin users-by-filter access denied"
                )

                println(
                    "Account ID: ${principal.accountId}"
                )

                println(
                    "JWT role: ${principal.role}"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "Admin access is required."
                    )
                )
            }

            AdminUsersByFilterResult.FilterRequired -> {
                println(
                    "Admin users-by-filter request has no filter"
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

            AdminUsersByFilterResult.Failed -> {
                println(
                    "Admin users-by-filter service failed"
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