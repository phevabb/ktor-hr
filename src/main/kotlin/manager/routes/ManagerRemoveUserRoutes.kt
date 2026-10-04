package com.hr.manager.routes

import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.manager.dtos.ManagerRemoveUserRequest
import com.hr.manager.services.ManagerRemoveUserResult
import com.hr.manager.services.ManagerRemoveUserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val managerRemoveUserJson =
    Json {
        ignoreUnknownKeys =
            true

        isLenient =
            true

        explicitNulls =
            false

        coerceInputValues =
            false
    }

fun Route.managerRemoveUserRoutes() {
    post("/remove-user") {
        println(
            "=================================================="
        )

        println(
            "POST /api/manager/remove-user request received"
        )

        val principal =
            call.principal<AuthPrincipal>()
                ?: run {
                    println(
                        "Manager remove-user request rejected"
                    )

                    println(
                        "Reason: Authentication is required"
                    )

                    println(
                        "=================================================="
                    )

                    return@post call.respond(
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

        val rawRequestBody =
            try {
                call.receiveText()
            } catch (exception: Exception) {
                println(
                    "Unable to read remove-user request body"
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
                    mapOf(
                        "detail" to
                                "The remove-user request body could not be read."
                    )
                )
            }

        if (rawRequestBody.isBlank()) {
            println(
                "Remove-user request body is empty"
            )

            println(
                "=================================================="
            )

            return@post call.respond(
                HttpStatusCode.BadRequest,
                mapOf(
                    "detail" to
                            "The remove-user request body is required."
                )
            )
        }

        println(
            "Incoming remove-user request:"
        )

        println(
            rawRequestBody
        )

        val request =
            try {
                managerRemoveUserJson
                    .decodeFromString<ManagerRemoveUserRequest>(
                        rawRequestBody
                    )
            } catch (exception: Exception) {
                println(
                    "Unable to decode ManagerRemoveUserRequest"
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

                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "detail" to
                                (
                                        exception.message
                                            ?: "user_id and reason are required."
                                        )
                    )
                )
            }

        println(
            "Remove-user request decoded successfully"
        )

        println(
            "Target account ID: ${request.accountId}"
        )

        println(
            "Removal reason: ${request.reason}"
        )

        val result =
            try {
                dbQuery {
                    ManagerRemoveUserService
                        .removeUser(
                            managerAccountId =
                                principal.accountId,

                            request =
                                request
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Unable to process Manager remove-user request"
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

                return@post call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "The user could not be removed."
                    )
                )
            }

        when (result) {
            is ManagerRemoveUserResult.Success -> {
                println(
                    "User removed successfully"
                )

                println(
                    "Removed account ID: ${result.response.accountId}"
                )

                println(
                    "Removed user ID: ${result.response.userId}"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.OK,
                    result.response
                )
            }

            ManagerRemoveUserResult.AccessDenied -> {
                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "Manager access is required."
                    )
                )
            }

            ManagerRemoveUserResult.ManagerAccountNotFound -> {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf(
                        "detail" to
                                "The authenticated Manager account was not found."
                    )
                )
            }

            ManagerRemoveUserResult.ManagerAccountInactive -> {
                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "This Manager account is inactive."
                    )
                )
            }

            ManagerRemoveUserResult.ManagerRegionNotAssigned -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "detail" to
                                "A region has not been assigned to this Manager account."
                    )
                )
            }

            ManagerRemoveUserResult.InvalidAccountId -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "user_id" to
                                listOf(
                                    "A valid user ID is required."
                                )
                    )
                )
            }

            ManagerRemoveUserResult.ReasonRequired -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "reason" to
                                listOf(
                                    "A removal reason is required."
                                )
                    )
                )
            }

            ManagerRemoveUserResult.ReasonTooLong -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "reason" to
                                listOf(
                                    "The removal reason must not exceed 255 characters."
                                )
                    )
                )
            }

            ManagerRemoveUserResult.CannotRemoveSelf -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "detail" to
                                "You cannot remove your own Manager account."
                    )
                )
            }

            ManagerRemoveUserResult.UserNotFound -> {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf(
                        "detail" to
                                "User not found."
                    )
                )
            }

            ManagerRemoveUserResult.UserAlreadyInactive -> {
                call.respond(
                    HttpStatusCode.Conflict,
                    mapOf(
                        "detail" to
                                "The selected user is already inactive."
                    )
                )
            }

            ManagerRemoveUserResult.UserRegionNotAssigned -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "detail" to
                                "The selected user does not have an assigned region."
                    )
                )
            }

            ManagerRemoveUserResult.UserOutsideManagerRegion -> {
                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "You cannot remove users outside your assigned region."
                    )
                )
            }

            ManagerRemoveUserResult.Failed -> {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "The user could not be removed."
                    )
                )
            }
        }
    }
}