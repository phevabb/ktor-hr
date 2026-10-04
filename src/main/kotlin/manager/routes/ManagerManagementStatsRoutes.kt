package com.hr.manager.routes

import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.manager.services.ManagerManagementStatsResult
import com.hr.manager.services.ManagerManagementStatsService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.managerManagementStatsRoutes() {
    get("/management-stats") {
        println(
            "=================================================="
        )

        println(
            "GET /api/manager/management-stats request received"
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
                .queryParameters["page_size"]
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
                    ManagerManagementStatsService
                        .getManagementStats(
                            managerAccountId =
                                principal.accountId,

                            page =
                                page,

                            pageSize =
                                pageSize,

                            requestPath =
                                "/api/manager/management-stats"
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Unable to process Manager management statistics request"
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
                                "Management-unit statistics could not be retrieved."
                    )
                )
            }

        when (result) {
            is ManagerManagementStatsResult.Success -> {
                println(
                    "Manager management statistics returned successfully"
                )

                println(
                    "Total records: ${result.statistics.count}"
                )

                println(
                    "Results returned: ${result.statistics.results.size}"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.OK,
                    result.statistics
                )
            }

            ManagerManagementStatsResult.AccessDenied -> {
                println(
                    "Manager management statistics access denied"
                )

                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "Manager access is required."
                    )
                )
            }

            ManagerManagementStatsResult.AccountNotFound -> {
                println(
                    "Authenticated Manager account was not found"
                )

                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf(
                        "detail" to
                                "The authenticated Manager account was not found."
                    )
                )
            }

            ManagerManagementStatsResult.AccountInactive -> {
                println(
                    "Inactive Manager attempted to retrieve management statistics"
                )

                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "This Manager account is inactive."
                    )
                )
            }

            ManagerManagementStatsResult.RegionNotAssigned -> {
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

            ManagerManagementStatsResult.Failed -> {
                println(
                    "Manager management statistics service failed"
                )

                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "Management-unit statistics could not be retrieved."
                    )
                )
            }
        }
    }
}