package com.hr.admin.routes

import com.hr.admin.services.AdminUserFieldsResult
import com.hr.admin.services.AdminUserFieldsService
import com.hr.auth.dtos.AuthMessageResponse
import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.userFieldsRoutes() {
    authenticate(
        "auth-jwt"
    ) {
        get(
            "/user-fields"
        ) {
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
                            "User-fields request rejected"
                        )

                        println(
                            "Reason: Authentication is required"
                        )

                        println(
                            "=================================================="
                        )

                        return@get call.respond(
                            HttpStatusCode.Unauthorized,
                            AuthMessageResponse(
                                detail =
                                    "Authentication is required."
                            )
                        )
                    }

            println(
                "User fields requested:"
            )

            println(
                "Authenticated account ID: ${principal.accountId}"
            )

            println(
                "Authenticated user ID: ${principal.userId}"
            )

            println(
                "Authenticated role: ${principal.role}"
            )

            println(
                "Calling AdminUserFieldsService"
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
                        "User-fields request failed"
                    )

                    println(
                        "Account ID: ${principal.accountId}"
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
                        AuthMessageResponse(
                            detail =
                                "User field metadata could not be retrieved."
                        )
                    )
                }

            when (result) {
                is AdminUserFieldsResult.Success -> {
                    println(
                        "User fields returned successfully"
                    )

                    println(
                        "Account ID: ${principal.accountId}"
                    )

                    println(
                        "Role: ${principal.role}"
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
                    println(
                        "Authenticated account was not found"
                    )

                    println(
                        "Account ID: ${principal.accountId}"
                    )

                    println(
                        "=================================================="
                    )

                    call.respond(
                        HttpStatusCode.NotFound,
                        AuthMessageResponse(
                            detail =
                                "The authenticated account was not found."
                        )
                    )
                }

                AdminUserFieldsResult.AccountInactive -> {
                    println(
                        "Inactive account attempted to retrieve user fields"
                    )

                    println(
                        "Account ID: ${principal.accountId}"
                    )

                    println(
                        "=================================================="
                    )

                    call.respond(
                        HttpStatusCode.Forbidden,
                        AuthMessageResponse(
                            detail =
                                "This account is inactive."
                        )
                    )
                }

                AdminUserFieldsResult.Failed -> {
                    println(
                        "User-fields service failed"
                    )

                    println(
                        "Account ID: ${principal.accountId}"
                    )

                    println(
                        "=================================================="
                    )

                    call.respond(
                        HttpStatusCode.InternalServerError,
                        AuthMessageResponse(
                            detail =
                                "User field metadata could not be retrieved."
                        )
                    )
                }
            }
        }
    }
}