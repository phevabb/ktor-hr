package com.hr.services

import com.auth0.jwt.exceptions.JWTVerificationException
import com.hr.auth.config.JwtConfig
import com.hr.auth.dtos.AuthenticatedUserResponse
import com.hr.auth.dtos.LoginRequest
import com.hr.auth.dtos.LoginResponse
import com.hr.auth.models.AuthResult
import com.hr.auth.repositories.AuthRepository
import com.hr.auth.security.PasswordHasher

object AuthService {

    suspend fun login(
        request: LoginRequest
    ): AuthResult {
        val normalizedUserId =
            request.userId.trim()

        if (
            normalizedUserId.isBlank() ||
            request.password.isBlank()
        ) {
            return AuthResult.InvalidCredentials
        }

        val account =
            AuthRepository
                .findAccountByUserId(
                    normalizedUserId
                )
                ?: return AuthResult
                    .InvalidCredentials

        val validPassword =
            PasswordHasher.verify(
                plainPassword =
                    request.password,
                passwordHash =
                    account.passwordHash
            )

        if (!validPassword) {
            return AuthResult.InvalidCredentials
        }

        if (!account.isActive) {
            return AuthResult.AccountInactive
        }

        val isManager =
            account.role.equals(
                other = "Manager",
                ignoreCase = true
            )

        val managerRegion =
            if (isManager) {
                AuthRepository
                    .findManagerRegion(
                        account.id
                    )
                    ?: return AuthResult
                        .ManagerProfileMissing
            } else {
                null
            }

        val generatedToken =
            JwtConfig.generateAccessToken(
                accountId =
                    account.id,
                userId =
                    account.userId,
                role =
                    account.role
            )

        val authenticatedUser =
            AuthenticatedUserResponse(
                id =
                    account.id,

                userId =
                    account.userId,

                role =
                    account.role,

                fullName =
                    account.fullName,

                displayName =
                    account.displayName,

                isActive =
                    account.isActive,

                isStaff =
                    account.isStaff,

                isSuperuser =
                    account.isSuperuser,

                regionId =
                    managerRegion?.regionId,

                regionName =
                    managerRegion?.regionName
            )

        println(
            "Login successful: " +
                    "accountId=${account.id}, " +
                    "userId=${account.userId}, " +
                    "role=${account.role}, " +
                    "region=${managerRegion?.regionName}"
        )

        return AuthResult.LoginSuccess(
            LoginResponse(
                token =
                    generatedToken.token,

                tokenType =
                    "Bearer",

                expiresAt =
                    generatedToken.expiresAt
                        .toString(),

                user =
                    authenticatedUser
            )
        )
    }

    suspend fun getAuthenticatedUser(
        accountId: Int
    ): AuthResult {
        val account =
            AuthRepository
                .findAccountById(
                    accountId
                )
                ?: return AuthResult
                    .AccountNotFound

        if (!account.isActive) {
            return AuthResult.AccountInactive
        }

        val isManager =
            account.role.equals(
                other = "Manager",
                ignoreCase = true
            )

        val managerRegion =
            if (isManager) {
                AuthRepository
                    .findManagerRegion(
                        account.id
                    )
                    ?: return AuthResult
                        .ManagerProfileMissing
            } else {
                null
            }

        val authenticatedUser =
            AuthenticatedUserResponse(
                id =
                    account.id,

                userId =
                    account.userId,

                role =
                    account.role,

                fullName =
                    account.fullName,

                displayName =
                    account.displayName,

                isActive =
                    account.isActive,

                isStaff =
                    account.isStaff,

                isSuperuser =
                    account.isSuperuser,

                regionId =
                    managerRegion?.regionId,

                regionName =
                    managerRegion?.regionName
            )

        return AuthResult.UserSuccess(
            authenticatedUser
        )
    }

    suspend fun changePassword(
        accountId: Int,
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): AuthResult {
        if (
            currentPassword.isBlank() ||
            newPassword.isBlank() ||
            confirmPassword.isBlank()
        ) {
            return AuthResult.PasswordFieldsRequired
        }

        val account =
            AuthRepository
                .findAccountById(
                    accountId
                )
                ?: return AuthResult
                    .AccountNotFound

        val currentPasswordValid =
            PasswordHasher.verify(
                plainPassword =
                    currentPassword,
                passwordHash =
                    account.passwordHash
            )

        if (!currentPasswordValid) {
            return AuthResult
                .CurrentPasswordIncorrect
        }

        if (newPassword != confirmPassword) {
            return AuthResult.PasswordMismatch
        }

        if (!isStrongPassword(newPassword)) {
            return AuthResult.WeakPassword
        }

        val passwordUnchanged =
            PasswordHasher.verify(
                plainPassword =
                    newPassword,
                passwordHash =
                    account.passwordHash
            )

        if (passwordUnchanged) {
            return AuthResult.PasswordUnchanged
        }

        val newPasswordHash =
            PasswordHasher.hash(
                newPassword
            )

        val updated =
            AuthRepository
                .updatePassword(
                    accountId =
                        accountId,
                    passwordHash =
                        newPasswordHash
                )

        if (!updated) {
            return AuthResult.OperationFailed
        }

        println(
            "Password changed successfully: " +
                    "accountId=$accountId"
        )

        return AuthResult.Success
    }

    suspend fun requestPasswordReset(
        userId: String
    ): PasswordResetRequestResult {
        val normalizedUserId =
            userId.trim()

        /*
         * Always return the same public message whether
         * the account exists or not. This prevents account
         * enumeration through the password-reset endpoint.
         */
        if (normalizedUserId.isBlank()) {
            return PasswordResetRequestResult(
                accepted = true,
                resetToken = null
            )
        }

        val account =
            AuthRepository
                .findAccountByUserId(
                    normalizedUserId
                )
                ?: return PasswordResetRequestResult(
                    accepted = true,
                    resetToken = null
                )

        if (!account.isActive) {
            return PasswordResetRequestResult(
                accepted = true,
                resetToken = null
            )
        }

        val passwordVersion =
            PasswordHasher.passwordVersion(
                account.passwordHash
            )

        val generatedToken =
            JwtConfig
                .generatePasswordResetToken(
                    accountId =
                        account.id,
                    passwordVersion =
                        passwordVersion
                )

        println(
            "Password reset requested: " +
                    "accountId=${account.id}, " +
                    "userId=${account.userId}, " +
                    "expiresAt=${generatedToken.expiresAt}"
        )

        /*
         * The raw token should be emailed or sent through
         * another secure delivery channel in production.
         *
         * Do not return resetToken from a public production
         * API response.
         */
        return PasswordResetRequestResult(
            accepted = true,
            resetToken =
                generatedToken.token
        )
    }

    suspend fun confirmPasswordReset(
        token: String,
        newPassword: String,
        confirmPassword: String
    ): AuthResult {
        if (
            token.isBlank() ||
            newPassword.isBlank() ||
            confirmPassword.isBlank()
        ) {
            return AuthResult.PasswordFieldsRequired
        }

        if (newPassword != confirmPassword) {
            return AuthResult.PasswordMismatch
        }

        if (!isStrongPassword(newPassword)) {
            return AuthResult.WeakPassword
        }

        val decodedToken =
            try {
                JwtConfig
                    .verifyPasswordResetToken(
                        token.trim()
                    )
            } catch (
                exception:
                JWTVerificationException
            ) {
                println(
                    "Password reset token verification failed: " +
                            exception.message
                )

                return AuthResult.InvalidResetToken
            } catch (exception: Exception) {
                println(
                    "Password reset token processing failed: " +
                            exception.message
                )

                return AuthResult.InvalidResetToken
            }

        val accountId =
            decodedToken
                .getClaim("accountId")
                .asInt()
                ?: return AuthResult
                    .InvalidResetToken

        val tokenPasswordVersion =
            decodedToken
                .getClaim(
                    "passwordVersion"
                )
                .asString()
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: return AuthResult
                    .InvalidResetToken

        val tokenType =
            decodedToken
                .getClaim("tokenType")
                .asString()

        if (tokenType != "password_reset") {
            return AuthResult.InvalidResetToken
        }

        val account =
            AuthRepository
                .findAccountById(
                    accountId
                )
                ?: return AuthResult
                    .InvalidResetToken

        if (!account.isActive) {
            return AuthResult.AccountInactive
        }

        val currentPasswordVersion =
            PasswordHasher.passwordVersion(
                account.passwordHash
            )

        if (
            currentPasswordVersion !=
            tokenPasswordVersion
        ) {
            /*
             * The password was already changed after this
             * reset token was issued. The token is no longer
             * valid.
             */
            return AuthResult.InvalidResetToken
        }

        val passwordUnchanged =
            PasswordHasher.verify(
                plainPassword =
                    newPassword,
                passwordHash =
                    account.passwordHash
            )

        if (passwordUnchanged) {
            return AuthResult.PasswordUnchanged
        }

        val newPasswordHash =
            PasswordHasher.hash(
                newPassword
            )

        val updated =
            AuthRepository
                .updatePassword(
                    accountId =
                        accountId,
                    passwordHash =
                        newPasswordHash
                )

        if (!updated) {
            return AuthResult.OperationFailed
        }

        println(
            "Password reset completed successfully: " +
                    "accountId=$accountId"
        )

        return AuthResult.Success
    }

    fun isStrongPassword(
        password: String
    ): Boolean {
        if (password.length < 10) {
            return false
        }

        val hasUppercase =
            password.any { character ->
                character.isUpperCase()
            }

        val hasLowercase =
            password.any { character ->
                character.isLowerCase()
            }

        val hasNumber =
            password.any { character ->
                character.isDigit()
            }

        val hasSpecialCharacter =
            password.any { character ->
                !character.isLetterOrDigit()
            }

        return (
                hasUppercase &&
                        hasLowercase &&
                        hasNumber &&
                        hasSpecialCharacter
                )
    }
}

data class PasswordResetRequestResult(
    val accepted: Boolean,
    val resetToken: String?
)