package com.hr.admin.routes

import com.hr.admin.services.ManagementUnitStatsResult
import com.hr.admin.services.ManagementUnitStatsService
import com.hr.auth.dtos.AuthMessageResponse
import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.managementUnitStatsRoutes() {
    authenticate("auth-jwt") {
        get("/management-stats") {
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

            val regionName =
                call.request
                    .queryParameters["region"]
                    ?.trim()
                    ?.takeIf {
                        it.isNotBlank()
                    }

            println(
                "Management unit statistics requested: " +
                        "accountId=${principal.accountId}, " +
                        "userId=${principal.userId}, " +
                        "role=${principal.role}, " +
                        "region=${regionName ?: "All regions"}, " +
                        "page=$page, " +
                        "pageSize=$pageSize"
            )

            val result =
                try {
                    dbQuery {
                        ManagementUnitStatsService
                            .getManagementUnitStats(
                                accountId =
                                    principal.accountId,

                                page =
                                    page,

                                pageSize =
                                    pageSize,

                                regionName =
                                    regionName,

                                requestPath =
                                    "/api/admin/management-stats"
                            )
                    }
                } catch (exception: Exception) {
                    println(
                        "Unable to retrieve management unit statistics: " +
                                "errorType=${exception::class.simpleName}, " +
                                "message=${exception.message}"
                    )

                    return@get call.respond(
                        HttpStatusCode.InternalServerError,
                        AuthMessageResponse(
                            detail =
                                "Management unit statistics could not be retrieved."
                        )
                    )
                }

            when (result) {
                is ManagementUnitStatsResult.Success -> {
                    call.respond(
                        HttpStatusCode.OK,
                        result.statistics
                    )
                }

                ManagementUnitStatsResult.AccessDenied -> {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        AuthMessageResponse(
                            detail =
                                "Admin access is required."
                        )
                    )
                }

                ManagementUnitStatsResult.AccountNotFound -> {
                    call.respond(
                        HttpStatusCode.NotFound,
                        AuthMessageResponse(
                            detail =
                                "The authenticated account was not found."
                        )
                    )
                }

                ManagementUnitStatsResult.AccountInactive -> {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        AuthMessageResponse(
                            detail =
                                "This account is inactive."
                        )
                    )
                }

                ManagementUnitStatsResult.Failed -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        AuthMessageResponse(
                            detail =
                                "Management unit statistics could not be retrieved."
                        )
                    )
                }
            }
        }
    }
}
