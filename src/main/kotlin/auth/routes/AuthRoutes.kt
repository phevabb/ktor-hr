package com.hr.auth.routes



import com.hr.auth.dtos.AuthMessageResponse
import com.hr.auth.dtos.ChangePasswordRequest
import com.hr.auth.dtos.LoginRequest
import com.hr.auth.dtos.PasswordResetConfirmRequest
import com.hr.auth.dtos.PasswordResetRequest
import com.hr.auth.models.AuthPrincipal
import com.hr.auth.models.AuthResult
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.services.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.authRoutes() {

    /*
     * Login
     *
     * POST /api/auth/login
     *
     * Public endpoint
     */
    post("/login") {
        val request =
            try {
                call.receive<LoginRequest>()
            } catch (exception: Exception) {
                println(
                    "Unable to read login request: ${exception.message}"
                )

                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    AuthMessageResponse(
                        detail =
                            "A valid user ID and password are required."
                    )
                )
            }

        println(
            "Login request received for user ID: ${request.userId}"
        )

        val result =
            try {
                dbQuery {
                    AuthService.login(
                        request
                    )
                }
            } catch (exception: Exception) {
                println(
                    "Login processing failed: ${exception.message}"
                )

                return@post call.respond(
                    HttpStatusCode.InternalServerError,
                    AuthMessageResponse(
                        detail =
                            "Login could not be completed."
                    )
                )
            }

        when (result) {
            is AuthResult.LoginSuccess -> {
                println(
                    "Login completed successfully: " +
                            "accountId=${result.response.user.id}, " +
                            "userId=${result.response.user.userId}"
                )

                call.respond(
                    HttpStatusCode.OK,
                    result.response
                )
            }

            AuthResult.InvalidCredentials -> {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    AuthMessageResponse(
                        detail =
                            "Invalid user ID or password."
                    )
                )
            }

            AuthResult.AccountInactive -> {
                call.respond(
                    HttpStatusCode.Forbidden,
                    AuthMessageResponse(
                        detail =
                            "This account is inactive. Please contact an administrator."
                    )
                )
            }

            AuthResult.ManagerProfileMissing -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    AuthMessageResponse(
                        detail =
                            "Manager profile not found. Please contact Head Office to assign a region."
                    )
                )
            }

            else -> {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    AuthMessageResponse(
                        detail =
                            "Login could not be completed."
                    )
                )
            }
        }
    }

    /*
     * Request password reset
     *
     * POST /api/auth/password-reset
     *
     * Public endpoint
     */
    post("/password-reset") {
        val request =
            try {
                call.receive<
                        PasswordResetRequest
                        >()
            } catch (exception: Exception) {
                println(
                    "Unable to read password-reset request: ${exception.message}"
                )

                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    AuthMessageResponse(
                        detail =
                            "A valid user ID is required."
                    )
                )
            }

        /*
         * Always return the same response whether the
         * account exists or not. This prevents someone
         * from using this endpoint to discover accounts.
         */
        try {
            val resetResult =
                dbQuery {
                    AuthService
                        .requestPasswordReset(
                            request.userId
                        )
                }

            if (
                resetResult.resetToken != null &&
                isLocalResetTokenOutputEnabled()
            ) {
                /*
                 * Local development only.
                 *
                 * Do not enable this in production.
                 * In production, send the reset token
                 * through email or another verified
                 * communication channel.
                 */
                println(
                    "LOCAL PASSWORD RESET TOKEN: " +
                            resetResult.resetToken
                )
            }
        } catch (exception: Exception) {
            /*
             * The public response remains generic.
             * Detailed information is written only
             * to the server console.
             */
            println(
                "Password-reset request processing failed: ${exception.message}"
            )
        }

        call.respond(
            HttpStatusCode.OK,
            AuthMessageResponse(
                detail =
                    "If an eligible account exists, password reset instructions have been sent."
            )
        )
    }

    /*
     * Confirm password reset
     *
     * POST /api/auth/password-reset-confirm
     *
     * Public endpoint
     */
    post("/password-reset-confirm") {
        val request =
            try {
                call.receive<
                        PasswordResetConfirmRequest
                        >()
            } catch (exception: Exception) {
                println(
                    "Unable to read password-reset confirmation: ${exception.message}"
                )

                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    AuthMessageResponse(
                        detail =
                            "The reset token and new passwords are required."
                    )
                )
            }

        println(
            "Password-reset confirmation received"
        )

        val result =
            try {
                dbQuery {
                    AuthService
                        .confirmPasswordReset(
                            token =
                                request.token,
                            newPassword =
                                request.newPassword,
                            confirmPassword =
                                request.confirmPassword
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Password-reset confirmation failed: ${exception.message}"
                )

                return@post call.respond(
                    HttpStatusCode.InternalServerError,
                    AuthMessageResponse(
                        detail =
                            "An error occurred while resetting the password."
                    )
                )
            }

        when (result) {
            AuthResult.Success -> {
                call.respond(
                    HttpStatusCode.OK,
                    AuthMessageResponse(
                        detail =
                            "Password has been reset successfully."
                    )
                )
            }

            AuthResult.PasswordFieldsRequired -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    AuthMessageResponse(
                        detail =
                            "The reset token and both password fields are required."
                    )
                )
            }

            AuthResult.PasswordMismatch -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    AuthMessageResponse(
                        detail =
                            "The new passwords do not match."
                    )
                )
            }

            AuthResult.WeakPassword -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    AuthMessageResponse(
                        detail =
                            passwordRequirementsMessage()
                    )
                )
            }

            AuthResult.PasswordUnchanged -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    AuthMessageResponse(
                        detail =
                            "The new password must be different from the current password."
                    )
                )
            }

            AuthResult.AccountInactive -> {
                call.respond(
                    HttpStatusCode.Forbidden,
                    AuthMessageResponse(
                        detail =
                            "This account is inactive. Please contact an administrator."
                    )
                )
            }

            AuthResult.InvalidResetToken -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    AuthMessageResponse(
                        detail =
                            "The password-reset token is invalid, expired, or has already been used."
                    )
                )
            }

            AuthResult.OperationFailed -> {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    AuthMessageResponse(
                        detail =
                            "The password could not be reset."
                    )
                )
            }

            else -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    AuthMessageResponse(
                        detail =
                            "The password-reset request could not be completed."
                    )
                )
            }
        }
    }

    /*
     * All routes inside this block require:
     *
     * Authorization: Bearer <access-token>
     */
    authenticate("auth-jwt") {

        /*
         * Get currently authenticated user
         *
         * GET /api/auth/me
         */
        get("/me") {
            val principal =
                call.principal<
                        AuthPrincipal
                        >()
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        AuthMessageResponse(
                            detail =
                                "Authentication is required."
                        )
                    )

            val result =
                try {
                    dbQuery {
                        AuthService
                            .getAuthenticatedUser(
                                principal.accountId
                            )
                    }
                } catch (exception: Exception) {
                    println(
                        "Unable to retrieve authenticated user: ${exception.message}"
                    )

                    return@get call.respond(
                        HttpStatusCode.InternalServerError,
                        AuthMessageResponse(
                            detail =
                                "The authenticated account could not be retrieved."
                        )
                    )
                }

            when (result) {
                is AuthResult.UserSuccess -> {
                    call.respond(
                        HttpStatusCode.OK,
                        result.user
                    )
                }

                AuthResult.AccountNotFound -> {
                    call.respond(
                        HttpStatusCode.NotFound,
                        AuthMessageResponse(
                            detail =
                                "Account not found."
                        )
                    )
                }

                AuthResult.AccountInactive -> {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        AuthMessageResponse(
                            detail =
                                "This account is inactive."
                        )
                    )
                }

                AuthResult.ManagerProfileMissing -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        AuthMessageResponse(
                            detail =
                                "Manager profile not found. Please contact Head Office to assign a region."
                        )
                    )
                }

                else -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        AuthMessageResponse(
                            detail =
                                "The authenticated account could not be retrieved."
                        )
                    )
                }
            }
        }

        /*
         * Logout
         *
         * POST /api/auth/logout
         */
        post("/logout") {
            val principal =
                call.principal<
                        AuthPrincipal
                        >()
                    ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        AuthMessageResponse(
                            detail =
                                "Authentication is required."
                        )
                    )

            println(
                "Logout requested: " +
                        "accountId=${principal.accountId}, " +
                        "userId=${principal.userId}, " +
                        "tokenId=${principal.tokenId}"
            )

            /*
             * JWT access tokens are stateless.
             *
             * The Vue application must delete the token
             * after this response.
             *
             * For immediate server-side revocation, add
             * refresh-token storage or a revoked-token
             * table using the JWT token ID.
             */
            call.respond(
                HttpStatusCode.OK,
                AuthMessageResponse(
                    detail =
                        "Successfully logged out."
                )
            )
        }

        /*
         * Change current password
         *
         * POST /api/auth/change-password
         */
        post("/change-password") {
            val principal =
                call.principal<
                        AuthPrincipal
                        >()
                    ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        AuthMessageResponse(
                            detail =
                                "Authentication is required."
                        )
                    )

            val request =
                try {
                    call.receive<
                            ChangePasswordRequest
                            >()
                } catch (exception: Exception) {
                    println(
                        "Unable to read change-password request: ${exception.message}"
                    )

                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        AuthMessageResponse(
                            detail =
                                "The current password and both new password fields are required."
                        )
                    )
                }

            val result =
                try {
                    dbQuery {
                        AuthService
                            .changePassword(
                                accountId =
                                    principal.accountId,

                                currentPassword =
                                    request.currentPassword,

                                newPassword =
                                    request.newPassword,

                                confirmPassword =
                                    request.confirmPassword
                            )
                    }
                } catch (exception: Exception) {
                    println(
                        "Change-password processing failed: ${exception.message}"
                    )

                    return@post call.respond(
                        HttpStatusCode.InternalServerError,
                        AuthMessageResponse(
                            detail =
                                "The password could not be changed."
                        )
                    )
                }

            when (result) {
                AuthResult.Success -> {
                    println(
                        "Password changed: " +
                                "accountId=${principal.accountId}"
                    )

                    call.respond(
                        HttpStatusCode.OK,
                        AuthMessageResponse(
                            detail =
                                "Password changed successfully."
                        )
                    )
                }

                AuthResult.PasswordFieldsRequired -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        AuthMessageResponse(
                            detail =
                                "The current password and both new password fields are required."
                        )
                    )
                }

                AuthResult.CurrentPasswordIncorrect -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        AuthMessageResponse(
                            detail =
                                "The current password is incorrect."
                        )
                    )
                }

                AuthResult.PasswordMismatch -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        AuthMessageResponse(
                            detail =
                                "The new passwords do not match."
                        )
                    )
                }

                AuthResult.WeakPassword -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        AuthMessageResponse(
                            detail =
                                passwordRequirementsMessage()
                        )
                    )
                }

                AuthResult.PasswordUnchanged -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        AuthMessageResponse(
                            detail =
                                "The new password must be different from the current password."
                        )
                    )
                }

                AuthResult.AccountNotFound -> {
                    call.respond(
                        HttpStatusCode.NotFound,
                        AuthMessageResponse(
                            detail =
                                "Account not found."
                        )
                    )
                }

                AuthResult.AccountInactive -> {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        AuthMessageResponse(
                            detail =
                                "This account is inactive."
                        )
                    )
                }

                AuthResult.OperationFailed -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        AuthMessageResponse(
                            detail =
                                "The password could not be changed."
                        )
                    )
                }

                else -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        AuthMessageResponse(
                            detail =
                                "The password-change request could not be completed."
                        )
                    )
                }
            }
        }
    }
}

private fun passwordRequirementsMessage(): String {
    return (
            "Password must contain at least 10 characters, " +
                    "an uppercase letter, a lowercase letter, " +
                    "a number, and a special character."
            )
}

private fun isLocalResetTokenOutputEnabled(): Boolean {
    return System.getenv(
        "PASSWORD_RESET_PRINT_TOKEN"
    )
        ?.trim()
        ?.equals(
            other = "true",
            ignoreCase = true
        )
        ?: false
}