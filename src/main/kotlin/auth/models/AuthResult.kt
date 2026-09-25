package com.hr.auth.models

import com.hr.auth.dtos.AuthenticatedUserResponse
import com.hr.auth.dtos.LoginResponse

sealed interface AuthResult {

    data class LoginSuccess(
        val response: LoginResponse
    ) : AuthResult

    data class UserSuccess(
        val user:
        AuthenticatedUserResponse
    ) : AuthResult

    data object InvalidCredentials :
        AuthResult

    data object AccountInactive :
        AuthResult

    data object ManagerProfileMissing :
        AuthResult

    data object AccountNotFound :
        AuthResult

    data object CurrentPasswordIncorrect :
        AuthResult

    data object PasswordFieldsRequired :
        AuthResult

    data object PasswordMismatch :
        AuthResult

    data object WeakPassword :
        AuthResult

    data object PasswordUnchanged :
        AuthResult

    data object InvalidResetToken :
        AuthResult

    data object OperationFailed :
        AuthResult

    data object Success :
        AuthResult
}