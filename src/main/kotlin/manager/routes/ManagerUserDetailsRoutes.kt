package com.hr.manager.routes

import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.manager.services.ManagerUserDetailsResult
import com.hr.manager.services.ManagerUserDetailsService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.managerUserDetailsRoutes() {
    get("/users/{userId}") {
        println(
            "=================================================="
        )

        println(
            "GET /api/manager/users/{userId} request received"
        )

        val principal =
            call.principal<AuthPrincipal>()

        if (principal == null) {
            println(
                "Manager user-details request has no authenticated principal"
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

        val requestedUserId =
            call.parameters["userId"]
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "userId" to
                                listOf(
                                    "A valid staff user ID is required."
                                )
                    )
                )

        println(
            "Requested staff user ID: $requestedUserId"
        )

        val result =
            try {
                dbQuery {
                    ManagerUserDetailsService
                        .getUserDetails(
                            managerAccountId =
                                principal.accountId,

                            requestedUserId =
                                requestedUserId
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Unable to process Manager user-details request"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
                )

                return@get call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "The requested user details could not be retrieved."
                    )
                )
            }

        when (result) {
            is ManagerUserDetailsResult.Success -> {
                println(
                    "Manager user-details request completed successfully"
                )

                println(
                    "Returned account ID: ${result.account.id}"
                )

                println(
                    "Returned user ID: ${result.account.userId}"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.OK,
                    result.account
                )
            }

            ManagerUserDetailsResult.AccessDenied -> {
                println(
                    "Manager user-details access denied"
                )

                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "Manager access is required."
                    )
                )
            }

            ManagerUserDetailsResult.ManagerAccountNotFound -> {
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

            ManagerUserDetailsResult.ManagerAccountInactive -> {
                println(
                    "Inactive Manager attempted to retrieve user details"
                )

                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "This Manager account is inactive."
                    )
                )
            }

            ManagerUserDetailsResult.ManagerRegionNotAssigned -> {
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

            ManagerUserDetailsResult.UserNotFound -> {
                println(
                    "Requested active user was not found"
                )

                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf(
                        "detail" to
                                "The requested active user was not found."
                    )
                )
            }

            ManagerUserDetailsResult.UserRegionNotAssigned -> {
                println(
                    "Requested user does not have an assigned region"
                )

                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf(
                        "detail" to
                                "The requested user does not have an assigned region."
                    )
                )
            }

            ManagerUserDetailsResult.UserOutsideManagerRegion -> {
                println(
                    "Requested user is outside the Manager's assigned region"
                )

                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "You cannot access users outside your assigned region."
                    )
                )
            }

            ManagerUserDetailsResult.Failed -> {
                println(
                    "Manager user-details service returned Failed"
                )

                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "The requested user details could not be retrieved."
                    )
                )
            }
        }
    }
}