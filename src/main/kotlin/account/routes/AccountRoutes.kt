package com.hr.account.routes

import com.hr.account.dtos.AccountCreateRequest
import com.hr.account.dtos.AccountResponse
import com.hr.account.repositories.AccountRepository
import com.hr.config.DatabaseFactory.dbQuery
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.accountRoutes() {

    /*
     * Get all accounts
     */

    get {

        val accounts = dbQuery {
            AccountRepository.getAll()
        }

        call.respond(
            HttpStatusCode.OK,
            accounts
        )
    }

    /*
     * Get one account
     */

    get("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Invalid account ID"
            )

        val account = dbQuery {
            AccountRepository.getById(id)
        } ?: return@get call.respond(
            HttpStatusCode.NotFound,
            "Account not found"
        )

        call.respond(
            HttpStatusCode.OK,
            account
        )
    }

    /*
     * Create account
     */

    post {

        val request =
            call.receive<AccountCreateRequest>()

        if (request.userId.isBlank()) {
            return@post call.respond(
                HttpStatusCode.BadRequest,
                "User ID is required"
            )
        }

        if (request.standardRetirementAge <= 0) {
            return@post call.respond(
                HttpStatusCode.BadRequest,
                "Standard retirement age must be greater than zero"
            )
        }

        val result = dbQuery {

            if (
                AccountRepository.userIdExists(
                    request.userId.trim()
                )
            ) {
                return@dbQuery AccountOperationResult.UserIdExists
            }

            if (!request.phoneNumber.isNullOrBlank()) {

                if (
                    AccountRepository.phoneNumberExists(
                        request.phoneNumber.trim()
                    )
                ) {
                    return@dbQuery AccountOperationResult.PhoneExists
                }
            }

            val accountId =
                AccountRepository.create(request)

            val account =
                AccountRepository.getById(accountId)
                    ?: return@dbQuery AccountOperationResult.Failed

            AccountOperationResult.Success(account)
        }

        when (result) {

            AccountOperationResult.UserIdExists -> {
                call.respond(
                    HttpStatusCode.Conflict,
                    "An account with this user ID already exists"
                )
            }

            AccountOperationResult.PhoneExists -> {
                call.respond(
                    HttpStatusCode.Conflict,
                    "An account with this phone number already exists"
                )
            }

            AccountOperationResult.Failed -> {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Account was created but could not be retrieved"
                )
            }

            AccountOperationResult.NotFound -> {
                call.respond(
                    HttpStatusCode.NotFound,
                    "Account not found"
                )
            }

            is AccountOperationResult.Success -> {
                println(
                    "Account created: ${result.account.userId}"
                )

                call.respond(
                    HttpStatusCode.Created,
                    result.account
                )
            }
        }
    }

    /*
     * Update account
     */

    put("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                "Invalid account ID"
            )

        val request =
            call.receive<AccountCreateRequest>()

        if (request.userId.isBlank()) {
            return@put call.respond(
                HttpStatusCode.BadRequest,
                "User ID is required"
            )
        }

        if (request.standardRetirementAge <= 0) {
            return@put call.respond(
                HttpStatusCode.BadRequest,
                "Standard retirement age must be greater than zero"
            )
        }

        val result = dbQuery {

            val existingAccount =
                AccountRepository.getById(id)
                    ?: return@dbQuery AccountOperationResult.NotFound

            val updated =
                AccountRepository.update(
                    id = id,
                    request = request
                )

            if (!updated) {
                return@dbQuery AccountOperationResult.Failed
            }

            val updatedAccount =
                AccountRepository.getById(id)
                    ?: return@dbQuery AccountOperationResult.Failed

            AccountOperationResult.Success(
                updatedAccount
            )
        }

        when (result) {

            AccountOperationResult.NotFound -> {
                call.respond(
                    HttpStatusCode.NotFound,
                    "Account not found"
                )
            }

            AccountOperationResult.Failed -> {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Account could not be updated"
                )
            }

            is AccountOperationResult.Success -> {
                println(
                    "Account updated: ${result.account.userId}"
                )

                call.respond(
                    HttpStatusCode.OK,
                    result.account
                )
            }

            AccountOperationResult.UserIdExists -> {
                call.respond(
                    HttpStatusCode.Conflict,
                    "An account with this user ID already exists"
                )
            }

            AccountOperationResult.PhoneExists -> {
                call.respond(
                    HttpStatusCode.Conflict,
                    "An account with this phone number already exists"
                )
            }
        }
    }

    /*
     * Delete account
     */

    delete("/{id}") {

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                "Invalid account ID"
            )

        val deleted = dbQuery {
            AccountRepository.delete(id)
        }

        if (deleted) {

            println("Account deleted: $id")

            call.respond(
                HttpStatusCode.OK,
                "Account deleted successfully"
            )

        } else {

            call.respond(
                HttpStatusCode.NotFound,
                "Account not found"
            )
        }
    }
}

private sealed interface AccountOperationResult {

    data object UserIdExists :
        AccountOperationResult

    data object PhoneExists :
        AccountOperationResult

    data object NotFound :
        AccountOperationResult

    data object Failed :
        AccountOperationResult

    data class Success(
        val account: AccountResponse
    ) : AccountOperationResult
}
