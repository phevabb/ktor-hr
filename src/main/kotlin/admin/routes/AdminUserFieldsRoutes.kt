package com.hr.admin.routes

import com.hr.admin.services.AdminUserFieldsResult
import com.hr.admin.services.AdminUserFieldsService
import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.adminUserFieldsRoutes() {
    get("/user-fields") {
        println(
            "=================================================="
        )

        println(
            "GET /api/admin/user-fields request received"
        )

        val principal =
            call.principal<AuthPrincipal>()
                ?: run {
                    println(
                        "Admin user-fields request rejected"
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

        val result =
            try {
                dbQuery {
                    AdminUserFieldsService
                        .getUserFields(
                            accountId =
                                principal.accountId
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Unable to process Admin user-fields request"
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
            is AdminUserFieldsResult.Success -> {
                println(
                    "Admin user fields returned successfully"
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

            AdminUserFieldsResult.AccountNotFound -> {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf(
                        "detail" to
                                "The authenticated Admin account was not found."
                    )
                )
            }

            AdminUserFieldsResult.AccountInactive -> {
                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "This Admin account is inactive."
                    )
                )
            }


            AdminUserFieldsResult.Failed -> {
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