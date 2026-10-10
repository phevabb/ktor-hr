package com.hr.password.route

import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.password.dto.ChangePasswordRequest
import com.hr.password.dto.ForgotPasswordRequest
import com.hr.password.dto.PasswordErrorResponse
import com.hr.password.dto.PasswordMessageResponse
import com.hr.password.dto.ResetPasswordRequest
import com.hr.password.security.PasswordSecurity
import com.hr.password.service.ChangePasswordResult
import com.hr.password.service.PasswordMailService
import com.hr.password.service.PasswordService
import com.hr.password.service.ResetPasswordResult
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.passwordRoutes() {
    route(
        "/password"
    ) {
        forgotPasswordRoute()

        resetPasswordRoute()

        authenticate(
            "auth-jwt"
        ) {
            changePasswordRoute()
        }
    }
}

private fun Route.forgotPasswordRoute() {
    post(
        "/forgot"
    ) {
        println(
            "=================================================="
        )

        println(
            "POST /api/password/forgot request received"
        )

        val request =
            try {
                call.receive<
                        ForgotPasswordRequest
                        >()
            } catch (exception: Exception) {
                println(
                    "Unable to receive forgot-password request"
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
                    PasswordErrorResponse(
                        error =
                            "A valid request body is required."
                    )
                )
            }

        val email =
            request.email
                .trim()
                .lowercase()

        if (
            email.isBlank() ||
            !isValidEmail(
                email
            )
        ) {
            println(
                "Forgot-password validation failed"
            )

            println(
                "Reason: Invalid email address"
            )

            println(
                "=================================================="
            )

            return@post call.respond(
                HttpStatusCode.BadRequest,
                PasswordErrorResponse(
                    error =
                        "A valid email address is required."
                )
            )
        }

        val genericResponseMessage =
            "If an active account matches that email address, a password reset link will be sent."

        val emailDetails =
            try {
                dbQuery {
                    PasswordService
                        .preparePasswordReset(
                            submittedEmail =
                                email
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Unable to prepare password-reset request"
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

                exception.printStackTrace()

                null
            }

        if (emailDetails != null) {
            try {
                PasswordMailService
                    .sendPasswordResetEmail(
                        recipientEmail =
                            emailDetails.email,

                        recipientName =
                            emailDetails.fullName,

                        resetToken =
                            emailDetails.rawToken
                    )

                println(
                    "Password-reset email sent successfully"
                )

                println(
                    "Recipient: ${emailDetails.email}"
                )
            } catch (exception: Exception) {
                println(
                    "Unable to send password-reset email"
                )

                println(
                    "Recipient: ${emailDetails.email}"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
                )

                exception.cause?.let { cause ->
                    println(
                        "Email cause type: ${cause::class.simpleName}"
                    )

                    println(
                        "Email cause message: ${cause.message}"
                    )
                }

                exception.printStackTrace()
            }
        } else {
            println(
                "No active account matched the submitted email"
            )

            println(
                "A generic response will still be returned"
            )
        }

        println(
            "POST /api/password/forgot completed with HTTP 200"
        )

        println(
            "=================================================="
        )

        call.respond(
            HttpStatusCode.OK,
            PasswordMessageResponse(
                message =
                    genericResponseMessage
            )
        )
    }
}

private fun Route.resetPasswordRoute() {
    post(
        "/reset"
    ) {
        println(
            "=================================================="
        )

        println(
            "POST /api/password/reset request received"
        )

        val request =
            try {
                call.receive<
                        ResetPasswordRequest
                        >()
            } catch (exception: Exception) {
                println(
                    "Unable to receive reset-password request"
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
                    PasswordErrorResponse(
                        error =
                            "A valid request body is required."
                    )
                )
            }

        val token =
            request.token.trim()

        if (token.isBlank()) {
            println(
                "Password-reset validation failed"
            )

            println(
                "Reason: Reset token is missing"
            )

            println(
                "=================================================="
            )

            return@post call.respond(
                HttpStatusCode.BadRequest,
                PasswordErrorResponse(
                    error =
                        "A password reset token is required."
                )
            )
        }

        if (
            request.newPassword !=
            request.confirmPassword
        ) {
            println(
                "Password-reset validation failed"
            )

            println(
                "Reason: Password confirmation does not match"
            )

            println(
                "=================================================="
            )

            return@post call.respond(
                HttpStatusCode.BadRequest,
                PasswordErrorResponse(
                    error =
                        "The password confirmation does not match."
                )
            )
        }

        val passwordValidationError =
            PasswordSecurity
                .validateNewPassword(
                    request.newPassword
                )

        if (
            passwordValidationError !=
            null
        ) {
            println(
                "Password-reset validation failed"
            )

            println(
                "Reason: $passwordValidationError"
            )

            println(
                "=================================================="
            )

            return@post call.respond(
                HttpStatusCode.BadRequest,
                PasswordErrorResponse(
                    error =
                        passwordValidationError
                )
            )
        }

        val result =
            try {
                dbQuery {
                    PasswordService
                        .resetPassword(
                            rawToken =
                                token,

                            newPassword =
                                request.newPassword
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Password-reset database operation failed"
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

                exception.printStackTrace()

                println(
                    "POST /api/password/reset completed with HTTP 500"
                )

                println(
                    "=================================================="
                )

                return@post call.respond(
                    HttpStatusCode.InternalServerError,
                    PasswordErrorResponse(
                        error =
                            "The password could not be reset."
                    )
                )
            }

        when (result) {
            is ResetPasswordResult.Success -> {
                val notification =
                    result.notificationDetails

                try {
                    PasswordMailService
                        .sendPasswordChangedEmail(
                            recipientEmail =
                                notification.email,

                            recipientName =
                                notification.fullName
                        )

                    println(
                        "Password-reset notification email sent"
                    )
                } catch (exception: Exception) {
                    println(
                        "Password was reset, but the notification email failed"
                    )

                    println(
                        "Recipient: ${notification.email}"
                    )

                    println(
                        "Error type: ${exception::class.simpleName}"
                    )

                    println(
                        "Error message: ${exception.message}"
                    )
                }

                println(
                    "Password reset completed successfully"
                )

                println(
                    "POST /api/password/reset completed with HTTP 200"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.OK,
                    PasswordMessageResponse(
                        message =
                            "Your password was reset successfully."
                    )
                )
            }

            ResetPasswordResult
                .InvalidOrExpiredToken -> {
                println(
                    "Password reset rejected"
                )

                println(
                    "Reason: Token is invalid, expired, or already used"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.BadRequest,
                    PasswordErrorResponse(
                        error =
                            "The password reset link is invalid or has expired."
                    )
                )
            }

            ResetPasswordResult
                .AccountNotFound -> {
                println(
                    "Password reset rejected"
                )

                println(
                    "Reason: Active account was not found"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.BadRequest,
                    PasswordErrorResponse(
                        error =
                            "The password reset link is invalid or has expired."
                    )
                )
            }

            ResetPasswordResult
                .NewPasswordMatchesCurrent -> {
                println(
                    "Password reset rejected"
                )

                println(
                    "Reason: New password matches current password"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.BadRequest,
                    PasswordErrorResponse(
                        error =
                            "The new password must be different from the current password."
                    )
                )
            }

            ResetPasswordResult
                .UpdateFailed -> {
                println(
                    "Password reset failed"
                )

                println(
                    "Reason: Password or token update failed"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.InternalServerError,
                    PasswordErrorResponse(
                        error =
                            "The password could not be reset."
                    )
                )
            }
        }
    }
}






private fun Route.changePasswordRoute() {
    post(
        "/change"
    ) {
        println(
            "=================================================="
        )

        println(
            "POST /api/password/change request received"
        )

        val principal =
            call.principal<
                    AuthPrincipal
                    >()
                ?: run {
                    println(
                        "Password change rejected"
                    )

                    println(
                        "Reason: Authentication is required"
                    )

                    println(
                        "=================================================="
                    )

                    return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        PasswordErrorResponse(
                            error =
                                "Authentication is required."
                        )
                    )
                }

        println(
            "Password change request authenticated"
        )

        println(
            "Authenticated account ID: ${principal.accountId}"
        )

        val request =
            try {
                call.receive<
                        ChangePasswordRequest
                        >()
            } catch (exception: Exception) {
                println(
                    "Unable to receive change-password request"
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
                    PasswordErrorResponse(
                        error =
                            "A valid request body is required."
                    )
                )
            }

        if (
            request.currentPassword
                .isBlank()
        ) {
            println(
                "Password-change validation failed"
            )

            println(
                "Reason: Current password is missing"
            )

            println(
                "=================================================="
            )

            return@post call.respond(
                HttpStatusCode.BadRequest,
                PasswordErrorResponse(
                    error =
                        "The current password is required."
                )
            )
        }

        if (
            request.newPassword !=
            request.confirmPassword
        ) {
            println(
                "Password-change validation failed"
            )

            println(
                "Reason: Password confirmation does not match"
            )

            println(
                "=================================================="
            )

            return@post call.respond(
                HttpStatusCode.BadRequest,
                PasswordErrorResponse(
                    error =
                        "The password confirmation does not match."
                )
            )
        }

        val passwordValidationError =
            PasswordSecurity
                .validateNewPassword(
                    request.newPassword
                )

        if (
            passwordValidationError !=
            null
        ) {
            println(
                "Password-change validation failed"
            )

            println(
                "Reason: $passwordValidationError"
            )

            println(
                "=================================================="
            )

            return@post call.respond(
                HttpStatusCode.BadRequest,
                PasswordErrorResponse(
                    error =
                        passwordValidationError
                )
            )
        }

        val result =
            try {
                dbQuery {
                    PasswordService
                        .changePassword(
                            accountId =
                                principal.accountId,

                            currentPassword =
                                request.currentPassword,

                            newPassword =
                                request.newPassword
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Password-change database operation failed"
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

                exception.printStackTrace()

                println(
                    "POST /api/password/change completed with HTTP 500"
                )

                println(
                    "=================================================="
                )

                return@post call.respond(
                    HttpStatusCode.InternalServerError,
                    PasswordErrorResponse(
                        error =
                            "The password could not be changed."
                    )
                )
            }

        when (result) {
            is ChangePasswordResult.Success -> {
                val notification =
                    result.notificationDetails

                try {
                    PasswordMailService
                        .sendPasswordChangedEmail(
                            recipientEmail =
                                notification.email,

                            recipientName =
                                notification.fullName
                        )

                    println(
                        "Password-change notification email sent"
                    )
                } catch (exception: Exception) {
                    println(
                        "Password changed, but the notification email failed"
                    )

                    println(
                        "Recipient: ${notification.email}"
                    )

                    println(
                        "Error type: ${exception::class.simpleName}"
                    )

                    println(
                        "Error message: ${exception.message}"
                    )
                }

                println(
                    "Password changed successfully"
                )

                println(
                    "Account ID: ${principal.accountId}"
                )

                println(
                    "POST /api/password/change completed with HTTP 200"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.OK,
                    PasswordMessageResponse(
                        message =
                            "Your password was changed successfully."
                    )
                )
            }

            ChangePasswordResult
                .AccountNotFound -> {
                println(
                    "Password change rejected"
                )

                println(
                    "Reason: Active account was not found"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.NotFound,
                    PasswordErrorResponse(
                        error =
                            "The authenticated account could not be found."
                    )
                )
            }

            ChangePasswordResult
                .CurrentPasswordIncorrect -> {
                println(
                    "Password change rejected"
                )

                println(
                    "Reason: Current password is incorrect"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.BadRequest,
                    PasswordErrorResponse(
                        error =
                            "The current password is incorrect."
                    )
                )
            }

            ChangePasswordResult
                .NewPasswordMatchesCurrent -> {
                println(
                    "Password change rejected"
                )

                println(
                    "Reason: New password matches current password"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.BadRequest,
                    PasswordErrorResponse(
                        error =
                            "The new password must be different from the current password."
                    )
                )
            }

            ChangePasswordResult
                .UpdateFailed -> {
                println(
                    "Password change failed"
                )

                println(
                    "Reason: Account update failed"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.InternalServerError,
                    PasswordErrorResponse(
                        error =
                            "The password could not be changed."
                    )
                )
            }
        }
    }
}











private fun isValidEmail(
    email: String
): Boolean {
    if (
        email.length >
        254
    ) {
        return false
    }

    val atIndex =
        email.indexOf(
            '@'
        )

    if (
        atIndex <= 0 ||
        atIndex !=
        email.lastIndexOf(
            '@'
        )
    ) {
        return false
    }

    val domain =
        email.substring(
            atIndex + 1
        )

    return (
            domain.isNotBlank() &&
                    domain.contains(
                        '.'
                    ) &&
                    !domain.startsWith(
                        '.'
                    ) &&
                    !domain.endsWith(
                        '.'
                    )
            )
}