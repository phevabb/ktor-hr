package com.hr.manager.routes

import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.manager.services.ManagerClassStatsResult
import com.hr.manager.services.ManagerClassStatsService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.managerClassStatsRoutes() {
    get("/class-stats") {
        println(
            "=================================================="
        )

        println(
            "GET /api/manager/class-stats request received"
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
                    ManagerClassStatsService
                        .getClassStats(
                            managerAccountId =
                                principal.accountId,

                            page =
                                page,

                            pageSize =
                                pageSize,

                            requestPath =
                                "/api/manager/class-stats"
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Unable to process Manager class statistics request"
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
                                "Class statistics could not be retrieved."
                    )
                )
            }

        when (result) {
            is ManagerClassStatsResult.Success -> {
                println(
                    "Manager class statistics returned successfully"
                )

                println(
                    "Total class records: ${result.statistics.count}"
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

            ManagerClassStatsResult.AccessDenied -> {
                println(
                    "Manager class statistics access denied"
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

            ManagerClassStatsResult.AccountNotFound -> {
                println(
                    "Authenticated Manager account was not found"
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

            ManagerClassStatsResult.AccountInactive -> {
                println(
                    "Inactive Manager attempted to retrieve class statistics"
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

            ManagerClassStatsResult.RegionNotAssigned -> {
                println(
                    "Manager does not have an assigned region"
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

            ManagerClassStatsResult.Failed -> {
                println(
                    "Manager class statistics service failed"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "Class statistics could not be retrieved."
                    )
                )
            }
        }
    }
}