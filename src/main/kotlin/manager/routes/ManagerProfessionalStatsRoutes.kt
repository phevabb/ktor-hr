package com.hr.manager.routes

import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.manager.services.ManagerProfessionalStatsResult
import com.hr.manager.services.ManagerProfessionalStatsService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.managerProfessionalStatsRoutes() {
    get("/pro-stats") {
        println(
            "=================================================="
        )

        println(
            "GET /api/manager/pro-stats request received"
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
                    ManagerProfessionalStatsService
                        .getProfessionalStats(
                            managerAccountId =
                                principal.accountId,

                            page =
                                page,

                            pageSize =
                                pageSize,

                            requestPath =
                                "/api/manager/pro-stats"
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Unable to process Manager professional statistics request"
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
                                "Professional statistics could not be retrieved."
                    )
                )
            }

        when (result) {
            is ManagerProfessionalStatsResult.Success -> {
                println(
                    "Manager professional statistics returned successfully"
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

            ManagerProfessionalStatsResult.AccessDenied -> {
                println(
                    "Manager professional statistics access denied"
                )

                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "Manager access is required."
                    )
                )
            }

            ManagerProfessionalStatsResult.AccountNotFound -> {
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

            ManagerProfessionalStatsResult.AccountInactive -> {
                println(
                    "Inactive Manager attempted to retrieve professional statistics"
                )

                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "This Manager account is inactive."
                    )
                )
            }

            ManagerProfessionalStatsResult.RegionNotAssigned -> {
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

            ManagerProfessionalStatsResult.Failed -> {
                println(
                    "Manager professional statistics service failed"
                )

                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "Professional statistics could not be retrieved."
                    )
                )
            }
        }
    }
}