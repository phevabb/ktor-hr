package com.hr.admin.routes

import com.hr.admin.services.UserFieldsResult
import com.hr.admin.services.UserFieldsService
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
    authenticate("auth-jwt") {
        get("/user-fields") {
            val principal =
                call.principal<AuthPrincipal>()
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        AuthMessageResponse(
                            detail =
                                "Authentication is required."
                        )
                    )

            println(
                "User fields requested: " +
                        "accountId=${principal.accountId}, " +
                        "userId=${principal.userId}, " +
                        "role=${principal.role}"
            )

            val result =
                try {
                    dbQuery {
                        UserFieldsService
                            .getUserFields(
                                accountId =
                                    principal.accountId
                            )
                    }
                } catch (exception: Exception) {
                    println(
                        "User fields request failed: " +
                                "errorType=${exception::class.simpleName}, " +
                                "message=${exception.message}"
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
                is UserFieldsResult.Success -> {
                    println(
                        "User fields returned successfully: " +
                                "accountId=${principal.accountId}, " +
                                "fieldCount=${result.fields.size}"
                    )

                    call.respond(
                        HttpStatusCode.OK,
                        result.fields
                    )
                }

                UserFieldsResult.AccessDenied -> {
                    println(
                        "User fields access denied: " +
                                "accountId=${principal.accountId}, " +
                                "role=${principal.role}"
                    )

                    call.respond(
                        HttpStatusCode.Forbidden,
                        AuthMessageResponse(
                            detail =
                                "Admin access is required."
                        )
                    )
                }

                UserFieldsResult.AccountNotFound -> {
                    println(
                        "Authenticated account not found: " +
                                "accountId=${principal.accountId}"
                    )

                    call.respond(
                        HttpStatusCode.NotFound,
                        AuthMessageResponse(
                            detail =
                                "The authenticated account was not found."
                        )
                    )
                }

                UserFieldsResult.AccountInactive -> {
                    println(
                        "Inactive account attempted to retrieve user fields: " +
                                "accountId=${principal.accountId}"
                    )

                    call.respond(
                        HttpStatusCode.Forbidden,
                        AuthMessageResponse(
                            detail =
                                "This account is inactive."
                        )
                    )
                }

                UserFieldsResult.Failed -> {
                    println(
                        "User fields service failed: " +
                                "accountId=${principal.accountId}"
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
