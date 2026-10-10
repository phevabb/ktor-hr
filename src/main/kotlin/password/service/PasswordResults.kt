package com.hr.password.service

data class PasswordNotificationDetails(
    val email: String,
    val fullName: String
)

sealed interface ResetPasswordResult {

    data class Success(
        val notificationDetails: PasswordNotificationDetails
    ) : ResetPasswordResult

    data object InvalidOrExpiredToken :
        ResetPasswordResult

    data object AccountNotFound :
        ResetPasswordResult

    data object NewPasswordMatchesCurrent :
        ResetPasswordResult

    data object UpdateFailed :
        ResetPasswordResult
}

sealed interface ChangePasswordResult {

    data class Success(
        val notificationDetails: PasswordNotificationDetails
    ) : ChangePasswordResult

    data object AccountNotFound :
        ChangePasswordResult

    data object CurrentPasswordIncorrect :
        ChangePasswordResult

    data object NewPasswordMatchesCurrent :
        ChangePasswordResult

    data object UpdateFailed :
        ChangePasswordResult
}

data class PasswordResetEmailDetails(
    val email: String,
    val fullName: String,
    val rawToken: String
)
