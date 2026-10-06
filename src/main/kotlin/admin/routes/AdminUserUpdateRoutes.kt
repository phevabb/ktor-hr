package com.hr.admin.routes

import com.hr.admin.services.AdminProfilePictureStorage
import com.hr.admin.services.AdminUserUpdateMultipartParser
import com.hr.admin.services.AdminUserUpdateResult
import com.hr.admin.services.AdminUserUpdateService
import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.patch

fun Route.adminUserUpdateRoutes(
    profilePictureStorage:
    AdminProfilePictureStorage
) {
    patch(
        "/user-update/{accountId}"
    ) {
        println(
            "=================================================="
        )

        println(
            "PATCH /api/admin/user-update/{accountId} request received"
        )

        val principal =
            call.principal<AuthPrincipal>()
                ?: run {
                    println(
                        "Admin user-update request rejected"
                    )

                    println(
                        "Reason: Authentication is required"
                    )

                    println(
                        "=================================================="
                    )

                    return@patch call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf(
                            "detail" to
                                    "Authentication is required."
                        )
                    )
                }

        val accountId =
            call.parameters[
                "accountId"
            ]
                ?.trim()
                ?.toIntOrNull()
                ?.takeIf {
                    it > 0
                }
                ?: run {
                    println(
                        "Admin user-update request rejected"
                    )

                    println(
                        "Reason: Account ID is invalid"
                    )

                    println(
                        "Received account ID: ${
                            call.parameters["accountId"]
                        }"
                    )

                    println(
                        "=================================================="
                    )

                    return@patch call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf(
                            "accountId" to
                                    listOf(
                                        "A valid account ID is required."
                                    )
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
            "Authenticated role: ${principal.role}"
        )

        println(
            "Target account ID: $accountId"
        )

        /*
         * Multipart data must be parsed before entering
         * the database transaction.
         */
        val multipartData =
            try {
                AdminUserUpdateMultipartParser
                    .parse(
                        call
                    )
            } catch (exception: Exception) {
                println(
                    "Unable to parse multipart update request"
                )

                println(
                    "Authenticated account ID: ${principal.accountId}"
                )

                println(
                    "Target account ID: $accountId"
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

                return@patch call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "detail" to
                                "The update request could not be parsed."
                    )
                )
            }

        println(
            "Update request parsed successfully"
        )

        println(
            "Received fields: ${multipartData.request.providedFields}"
        )

        println(
            "Received field count: ${
                multipartData.request.providedFields.size
            }"
        )

        println(
            "Profile picture included: ${
                multipartData.profilePicture != null
            }"
        )

        println(
            "Remove profile picture: ${
                multipartData.request.removeProfilePicture
            }"
        )

        multipartData.profilePicture?.let { picture ->
            println(
                "Profile picture file name: ${picture.originalFileName}"
            )

            println(
                "Profile picture content type: ${
                    picture.contentType ?: "Not provided"
                }"
            )

            println(
                "Profile picture size: ${picture.bytes.size} bytes"
            )
        }

        val result =
            try {
                dbQuery {
                    AdminUserUpdateService
                        .updateUser(
                            authenticatedAccountId =
                                principal.accountId,

                            targetAccountId =
                                accountId,

                            request =
                                multipartData.request,

                            profilePicture =
                                multipartData.profilePicture,

                            profilePictureStorage =
                                profilePictureStorage
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Unable to process Admin user-update request"
                )

                println(
                    "Authenticated account ID: ${principal.accountId}"
                )

                println(
                    "Target account ID: $accountId"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
                )

                var currentCause =
                    exception.cause

                var causeLevel =
                    1

                while (currentCause != null) {
                    println(
                        "Cause $causeLevel type: ${
                            currentCause::class.simpleName
                        }"
                    )

                    println(
                        "Cause $causeLevel message: ${
                            currentCause.message
                        }"
                    )

                    currentCause =
                        currentCause.cause

                    causeLevel +=
                        1
                }

                println(
                    "=================================================="
                )

                return@patch call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "The account could not be updated."
                    )
                )
            }

        when (result) {
            is AdminUserUpdateResult.Success -> {
                println(
                    "Admin user-update completed successfully"
                )

                println(
                    "Authenticated account ID: ${principal.accountId}"
                )

                println(
                    "Updated account ID: ${result.account.id}"
                )

                println(
                    "Updated user ID: ${result.account.userId}"
                )

                println(
                    "Updated full name: ${result.account.fullName}"
                )

                println(
                    "Updated profile picture URL: ${
                        result.account.profilePictureUrl
                            ?: "Not set"
                    }"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.OK,
                    result.account
                )
            }

            AdminUserUpdateResult.AccountNotFound -> {
                println(
                    "Admin user-update target account was not found"
                )

                println(
                    "Target account ID: $accountId"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf(
                        "detail" to
                                "The account was not found."
                    )
                )
            }

            AdminUserUpdateResult.AuthenticatedAccountNotFound -> {
                println(
                    "Authenticated account was not found"
                )

                println(
                    "Authenticated account ID: ${principal.accountId}"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf(
                        "detail" to
                                "The authenticated account was not found."
                    )
                )
            }

            AdminUserUpdateResult.AuthenticatedAccountInactive -> {
                println(
                    "Inactive account attempted to update an account"
                )

                println(
                    "Authenticated account ID: ${principal.accountId}"
                )

                println(
                    "Target account ID: $accountId"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "The authenticated account is inactive."
                    )
                )
            }

            AdminUserUpdateResult.InvalidRequest -> {
                println(
                    "Admin user-update request is invalid"
                )

                println(
                    "Target account ID: $accountId"
                )

                println(
                    "Received fields: ${
                        multipartData.request.providedFields
                    }"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "detail" to
                                "The update request is invalid."
                    )
                )
            }

            is AdminUserUpdateResult.ValidationFailed -> {
                println(
                    "Admin user-update validation failed"
                )

                println(
                    "Target account ID: $accountId"
                )

                println(
                    "Validation errors: ${result.errors}"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.UnprocessableEntity,
                    result.errors
                )
            }

            AdminUserUpdateResult.Failed -> {
                println(
                    "Admin user-update service failed"
                )

                println(
                    "Authenticated account ID: ${principal.accountId}"
                )

                println(
                    "Target account ID: $accountId"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "The account could not be updated."
                    )
                )
            }
        }
    }
}