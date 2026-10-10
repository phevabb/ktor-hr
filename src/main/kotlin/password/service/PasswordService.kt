package com.hr.password.service

import com.hr.password.repository.PasswordRepository
import com.hr.password.security.PasswordSecurity
import java.time.LocalDateTime

object PasswordService {

    private const val RESET_TOKEN_EXPIRY_MINUTES =
        30L

    suspend fun preparePasswordReset(
        submittedEmail: String
    ): PasswordResetEmailDetails? {
        val normalizedEmail =
            submittedEmail
                .trim()
                .lowercase()

        if (normalizedEmail.isBlank()) {
            return null
        }

        val account =
            PasswordRepository
                .findActiveAccountByEmail(
                    normalizedEmail
                )
                ?: return null

        val rawToken =
            PasswordSecurity
                .generateResetToken()

        val tokenHash =
            PasswordSecurity
                .hashResetToken(
                    rawToken
                )

        val expiresAt =
            LocalDateTime
                .now()
                .plusMinutes(
                    RESET_TOKEN_EXPIRY_MINUTES
                )

        PasswordRepository
            .createResetToken(
                accountId =
                    account.id,

                tokenHash =
                    tokenHash,

                expiresAt =
                    expiresAt
            )

        println(
            "Password-reset token created"
        )

        println(
            "Account ID: ${account.id}"
        )

        println(
            "Reset token expires at: $expiresAt"
        )

        return PasswordResetEmailDetails(
            email =
                account.email,

            fullName =
                account.fullName,

            rawToken =
                rawToken
        )
    }

    suspend fun resetPassword(
        rawToken: String,
        newPassword: String
    ): ResetPasswordResult {
        val normalizedToken =
            rawToken.trim()

        if (normalizedToken.isBlank()) {
            return ResetPasswordResult
                .InvalidOrExpiredToken
        }

        val validToken =
            PasswordRepository
                .findValidResetToken(
                    normalizedToken
                )
                ?: return ResetPasswordResult
                    .InvalidOrExpiredToken

        val account =
            PasswordRepository
                .findActiveAccountById(
                    validToken.accountId
                )
                ?: return ResetPasswordResult
                    .AccountNotFound

        val matchesCurrentPassword =
            PasswordSecurity
                .verifyPassword(
                    plainPassword =
                        newPassword,

                    storedPasswordHash =
                        account.passwordHash
                )

        if (matchesCurrentPassword) {
            return ResetPasswordResult
                .NewPasswordMatchesCurrent
        }

        val newPasswordHash =
            PasswordSecurity
                .hashPassword(
                    newPassword
                )

        val passwordUpdated =
            PasswordRepository
                .updatePasswordAndUseToken(
                    accountId =
                        account.id,

                    tokenId =
                        validToken.tokenId,

                    newPasswordHash =
                        newPasswordHash
                )

        if (!passwordUpdated) {
            return ResetPasswordResult
                .UpdateFailed
        }

        println(
            "Password reset completed successfully"
        )

        println(
            "Account ID: ${account.id}"
        )

        return ResetPasswordResult
            .Success(
                notificationDetails =
                    PasswordNotificationDetails(
                        email =
                            account.email,

                        fullName =
                            account.fullName
                    )
            )
    }

    suspend fun changePassword(
        accountId: Int,
        currentPassword: String,
        newPassword: String
    ): ChangePasswordResult {
        val account =
            PasswordRepository
                .findActiveAccountById(
                    accountId
                )
                ?: return ChangePasswordResult
                    .AccountNotFound

        val currentPasswordIsCorrect =
            PasswordSecurity
                .verifyPassword(
                    plainPassword =
                        currentPassword,

                    storedPasswordHash =
                        account.passwordHash
                )

        if (!currentPasswordIsCorrect) {
            return ChangePasswordResult
                .CurrentPasswordIncorrect
        }

        val newPasswordMatchesCurrent =
            PasswordSecurity
                .verifyPassword(
                    plainPassword =
                        newPassword,

                    storedPasswordHash =
                        account.passwordHash
                )

        if (newPasswordMatchesCurrent) {
            return ChangePasswordResult
                .NewPasswordMatchesCurrent
        }

        val newPasswordHash =
            PasswordSecurity
                .hashPassword(
                    newPassword
                )

        val passwordUpdated =
            PasswordRepository
                .changePassword(
                    accountId =
                        account.id,

                    newPasswordHash =
                        newPasswordHash
                )

        if (!passwordUpdated) {
            return ChangePasswordResult
                .UpdateFailed
        }

        println(
            "Authenticated password change completed successfully"
        )

        println(
            "Account ID: ${account.id}"
        )

        return ChangePasswordResult
            .Success(
                notificationDetails =
                    PasswordNotificationDetails(
                        email =
                            account.email,

                        fullName =
                            account.fullName
                    )
            )
    }
}