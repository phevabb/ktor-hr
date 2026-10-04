package com.hr.manager.routes

import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.manager.services.ManagerUsersExcelResult
import com.hr.manager.services.ManagerUsersExcelService

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.managerUsersExcelRoutes() {
    get("/all-users-to-excel") {
        println(
            "=================================================="
        )

        println(
            "GET /api/manager/all-users-to-excel request received"
        )

        val principal =
            call.principal<AuthPrincipal>()
                ?: run {
                    println(
                        "Manager Excel request rejected"
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
            "JWT role: ${principal.role}"
        )

        val result =
            try {
                dbQuery {
                    ManagerUsersExcelService
                        .getAllUsersForExcel(
                            managerAccountId =
                                principal.accountId
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Unable to process Manager Excel export request"
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
                                "Regional staff export data could not be retrieved."
                    )
                )
            }

        when (result) {
            is ManagerUsersExcelResult.Success -> {
                println(
                    "Manager Excel export data returned successfully"
                )

                println(
                    "Export record count: ${result.users.size}"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.OK,
                    result.users
                )
            }

            ManagerUsersExcelResult.AccessDenied -> {
                println(
                    "Manager Excel export access denied"
                )

                println(
                    "Reason: Authenticated account does not have the Manager role"
                )

                println(
                    "Authenticated account ID: ${principal.accountId}"
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
                                "Manager access is required."
                    )
                )
            }

            ManagerUsersExcelResult.AccountNotFound -> {
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

            ManagerUsersExcelResult.AccountInactive -> {
                println(
                    "Inactive Manager attempted to export users"
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

            ManagerUsersExcelResult.RegionNotAssigned -> {
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

            ManagerUsersExcelResult.Failed -> {
                println(
                    "Manager Excel export service failed"
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
                                "Regional staff export data could not be retrieved."
                    )
                )
            }
        }
    }
}