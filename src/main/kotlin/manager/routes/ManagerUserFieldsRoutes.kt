package com.hr.manager.routes

import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.manager.services.ManagerUserFieldsResult
import com.hr.manager.services.ManagerUserFieldsService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.managerUserFieldsRoutes() {
    get("/user-fields") {
        println(
            "=================================================="
        )

        println(
            "GET /api/manager/user-fields request received"
        )

        val principal =
            call.principal<AuthPrincipal>()
                ?: run {
                    println(
                        "Manager user-fields request rejected"
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
                    ManagerUserFieldsService
                        .getUserFields(
                            managerAccountId =
                                principal.accountId
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Unable to process Manager user-fields request"
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
                                "The account fields could not be retrieved."
                    )
                )
            }

        when (result) {
            is ManagerUserFieldsResult.Success -> {
                println(
                    "Manager user fields returned successfully"
                )

                println(
                    "Field count: ${result.fields.size}"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.OK,
                    result.fields
                )
            }

            ManagerUserFieldsResult.AccessDenied -> {
                println(
                    "Manager user-fields access denied"
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

            ManagerUserFieldsResult.AccountNotFound -> {
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

            ManagerUserFieldsResult.AccountInactive -> {
                println(
                    "Inactive Manager attempted to retrieve user fields"
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

            ManagerUserFieldsResult.RegionNotAssigned -> {
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

            ManagerUserFieldsResult.Failed -> {
                println(
                    "Manager user-fields service failed"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "The account fields could not be retrieved."
                    )
                )
            }
        }
    }
}