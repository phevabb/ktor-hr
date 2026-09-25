package com.hr.admin.routes

import com.hr.admin.services.DepartmentStatsResult
import com.hr.admin.services.DepartmentStatsService
import com.hr.auth.dtos.AuthMessageResponse
import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.departmentStatsRoutes() {
    authenticate("auth-jwt") {
        get("/directorate-stats") {
            val principal =
                call.principal<AuthPrincipal>()
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        AuthMessageResponse(
                            detail =
                                "Authentication is required."
                        )
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
                "Department statistics requested: " +
                        "accountId=${principal.accountId}, " +
                        "userId=${principal.userId}, " +
                        "role=${principal.role}, " +
                        "page=$page, " +
                        "pageSize=$pageSize"
            )

            val result =
                try {
                    dbQuery {
                        DepartmentStatsService
                            .getDepartmentStats(
                                accountId =
                                    principal.accountId,

                                page =
                                    page,

                                pageSize =
                                    pageSize,

                                requestPath =
                                    "/api/admin/directorate-stats"
                            )
                    }
                } catch (exception: Exception) {
                    println(
                        "Unable to retrieve department statistics: " +
                                "errorType=${exception::class.simpleName}, " +
                                "message=${exception.message}"
                    )

                    return@get call.respond(
                        HttpStatusCode.InternalServerError,
                        AuthMessageResponse(
                            detail =
                                "Department statistics could not be retrieved."
                        )
                    )
                }

            when (result) {
                is DepartmentStatsResult.Success -> {
                    call.respond(
                        HttpStatusCode.OK,
                        result.statistics
                    )
                }

                DepartmentStatsResult.AccessDenied -> {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        AuthMessageResponse(
                            detail =
                                "Admin access is required."
                        )
                    )
                }

                DepartmentStatsResult.AccountNotFound -> {
                    call.respond(
                        HttpStatusCode.NotFound,
                        AuthMessageResponse(
                            detail =
                                "The authenticated account was not found."
                        )
                    )
                }

                DepartmentStatsResult.AccountInactive -> {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        AuthMessageResponse(
                            detail =
                                "This account is inactive."
                        )
                    )
                }

                DepartmentStatsResult.Failed -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        AuthMessageResponse(
                            detail =
                                "Department statistics could not be retrieved."
                        )
                    )
                }
            }
        }
    }
}