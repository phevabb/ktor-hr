package com.hr.removallogs.route

import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.removallogs.dto.RemoveAccountErrorResponse
import com.hr.removallogs.dto.RemoveAccountRequest
import com.hr.removallogs.dto.RemoveAccountSuccessResponse
import com.hr.removallogs.service.RemovalAuthorizationResult
import com.hr.removallogs.service.RemovalLogResult
import com.hr.removallogs.service.RemovalLogService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.removalLogRoutes() {
    post(
        "/api/v1/removal-logs/remove-user"
    ) {
        println(
            "=================================================="
        )

        println(
            "POST /api/v1/removal-logs/remove-user request received"
        )

        val principal =
            call.principal<
                    AuthPrincipal
                    >()
                ?: run {
                    println(
                        "Account removal rejected"
                    )

                    println(
                        "Reason: Authentication is required"
                    )

                    println(
                        "=================================================="
                    )

                    return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        RemoveAccountErrorResponse(
                            error =
                                "Authentication is required."
                        )
                    )
                }

        println(
            "Account removal request authenticated"
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

        val authorizationResult =
            RemovalLogService
                .authorizeRole(
                    role =
                        principal.role
                            .toString()
                )

        val authorizedRole =
            when (
                authorizationResult
            ) {
                is RemovalAuthorizationResult
                .Authorized -> {
                    authorizationResult
                        .normalizedRole
                }

                RemovalAuthorizationResult
                    .Forbidden -> {
                    println(
                        "Account removal rejected"
                    )

                    println(
                        "Reason: Authenticated role is not permitted"
                    )

                    println(
                        "=================================================="
                    )

                    return@post call.respond(
                        HttpStatusCode.Forbidden,
                        RemoveAccountErrorResponse(
                            error =
                                "Admin, Manager, or SuperAdmin access is required."
                        )
                    )
                }
            }

        val request =
            try {
                call.receive<
                        RemoveAccountRequest
                        >()
            } catch (exception: Exception) {
                println(
                    "Unable to receive account-removal request"
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

                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    RemoveAccountErrorResponse(
                        error =
                            "The removal request body is invalid."
                    )
                )
            }

        val accountId =
            request.userId

        if (
            accountId == null ||
            accountId <= 0
        ) {
            println(
                "Account-removal validation failed"
            )

            println(
                "Reason: user_id is required"
            )

            println(
                "=================================================="
            )

            return@post call.respond(
                HttpStatusCode.BadRequest,
                RemoveAccountErrorResponse(
                    error =
                        "user_id and reason are required"
                )
            )
        }

        val normalizedReason =
            RemovalLogService
                .normalizeReason(
                    request.reason
                )

        if (
            normalizedReason == null
        ) {
            println(
                "Account-removal validation failed"
            )

            println(
                "Reason: Invalid or missing removal reason"
            )

            println(
                "=================================================="
            )

            return@post call.respond(
                HttpStatusCode.BadRequest,
                RemoveAccountErrorResponse(
                    error =
                        "The reason must be Resigned, Retired, Terminated, or Other."
                )
            )
        }

        println(
            "Account-removal request validated"
        )

        println(
            "Account being removed: $accountId"
        )

        println(
            "Removal reason: $normalizedReason"
        )

        println(
            "Removal requested by role: $authorizedRole"
        )

        val result =
            try {
                dbQuery {
                    RemovalLogService
                        .removeAccount(
                            accountId =
                                accountId,

                            removedByAccountId =
                                principal.accountId,

                            removedByRole =
                                authorizedRole,

                            reason =
                                normalizedReason
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Account-removal database operation failed"
                )

                println(
                    "Account ID: $accountId"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
                )

                exception.cause?.let { cause ->
                    println(
                        "Database cause type: ${cause::class.simpleName}"
                    )

                    println(
                        "Database cause message: ${cause.message}"
                    )
                }

                exception.printStackTrace()

                println(
                    "POST /api/v1/removal-logs/remove-user completed with HTTP 500"
                )

                println(
                    "=================================================="
                )

                return@post call.respond(
                    HttpStatusCode.InternalServerError,
                    RemoveAccountErrorResponse(
                        error =
                            "The account could not be removed."
                    )
                )
            }

        when (result) {
            RemovalLogResult
                .AccountNotFound -> {
                println(
                    "Account removal failed: account not found"
                )

                println(
                    "Account ID: $accountId"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.NotFound,
                    RemoveAccountErrorResponse(
                        error =
                            "User not found"
                    )
                )
            }

            RemovalLogResult
                .ActorAccountNotFound -> {
                println(
                    "Account removal failed: authenticated actor account not found"
                )

                println(
                    "Actor account ID: ${principal.accountId}"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.Unauthorized,
                    RemoveAccountErrorResponse(
                        error =
                            "The authenticated account could not be found."
                    )
                )
            }

            RemovalLogResult
                .AlreadyInactive -> {
                println(
                    "Account removal rejected: account is already inactive"
                )

                println(
                    "Account ID: $accountId"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.Conflict,
                    RemoveAccountErrorResponse(
                        error =
                            "The account has already been removed."
                    )
                )
            }

            RemovalLogResult
                .CannotRemoveOwnAccount -> {
                println(
                    "Account removal rejected: self-removal is not permitted"
                )

                println(
                    "Account ID: $accountId"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.Conflict,
                    RemoveAccountErrorResponse(
                        error =
                            "You cannot remove your own account."
                    )
                )
            }

            RemovalLogResult
                .UpdateFailed -> {
                println(
                    "Account removal failed: account update affected an unexpected number of rows"
                )

                println(
                    "Account ID: $accountId"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.InternalServerError,
                    RemoveAccountErrorResponse(
                        error =
                            "The account could not be removed."
                    )
                )
            }

            RemovalLogResult
                .Success -> {
                println(
                    "Account removed successfully"
                )

                println(
                    "Removed account ID: $accountId"
                )

                println(
                    "Removed by account ID: ${principal.accountId}"
                )

                println(
                    "Removed by role: $authorizedRole"
                )

                println(
                    "Removal reason: $normalizedReason"
                )

                println(
                    "POST /api/v1/removal-logs/remove-user completed with HTTP 200"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.OK,
                    RemoveAccountSuccessResponse(
                        message =
                            "User removed successfully",

                        accountId =
                            accountId,

                        reason =
                            normalizedReason
                    )
                )
            }
        }
    }
}