package com.hr.admin.routes

import com.hr.admin.services.StaffCategoryStatsResult
import com.hr.admin.services.StaffCategoryStatsService
import com.hr.auth.dtos.AuthMessageResponse
import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.staffCategoryStatsRoutes() {
    authenticate("auth-jwt") {
        get("/senior-stats") {
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
                "Staff category statistics requested: " +
                        "accountId=${principal.accountId}, " +
                        "userId=${principal.userId}, " +
                        "role=${principal.role}, " +
                        "page=$page, " +
                        "pageSize=$pageSize"
            )

            val result =
                try {
                    dbQuery {
                        StaffCategoryStatsService
                            .getStaffCategoryStats(
                                accountId =
                                    principal.accountId,

                                page =
                                    page,

                                pageSize =
                                    pageSize,

                                requestPath =
                                    "/api/admin/senior-stats"
                            )
                    }
                } catch (exception: Exception) {
                    println(
                        "Unable to retrieve staff category statistics: " +
                                "errorType=${exception::class.simpleName}, " +
                                "message=${exception.message}"
                    )

                    return@get call.respond(
                        HttpStatusCode.InternalServerError,
                        AuthMessageResponse(
                            detail =
                                "Staff category statistics could not be retrieved."
                        )
                    )
                }

            when (result) {
                is StaffCategoryStatsResult.Success -> {
                    println(
                        "Staff category statistics returned successfully: " +
                                "count=${result.statistics.count}, " +
                                "results=${result.statistics.results}"
                    )

                    call.respond(
                        HttpStatusCode.OK,
                        result.statistics
                    )
                }

                StaffCategoryStatsResult.AccessDenied -> {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        AuthMessageResponse(
                            detail =
                                "Admin access is required."
                        )
                    )
                }

                StaffCategoryStatsResult.AccountNotFound -> {
                    call.respond(
                        HttpStatusCode.NotFound,
                        AuthMessageResponse(
                            detail =
                                "The authenticated account was not found."
                        )
                    )
                }

                StaffCategoryStatsResult.AccountInactive -> {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        AuthMessageResponse(
                            detail =
                                "This account is inactive."
                        )
                    )
                }

                StaffCategoryStatsResult.Failed -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        AuthMessageResponse(
                            detail =
                                "Staff category statistics could not be retrieved."
                        )
                    )
                }
            }
        }
    }
}