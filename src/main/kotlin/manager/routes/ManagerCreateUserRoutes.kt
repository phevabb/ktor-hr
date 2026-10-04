package com.hr.manager.routes

import com.hr.account.dtos.AccountCreateRequest
import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.manager.services.ManagerCreateUserResult
import com.hr.manager.services.ManagerCreateUserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private val managerCreateUserJson =
    Json {
        ignoreUnknownKeys =
            true

        isLenient =
            true

        explicitNulls =
            false

        coerceInputValues =
            false

        prettyPrint =
            true
    }

fun Route.managerCreateUserRoutes() {
    post("/users/create") {
        println(
            "=================================================="
        )

        println(
            "POST /api/manager/users/create request received"
        )

        val principal =
            call.principal<AuthPrincipal>()
                ?: run {
                    println(
                        "Manager account creation rejected"
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
                    "Unable to read Manager account creation request"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
                )

                printManagerCreateUserExceptionCauses(
                    exception
                )

                println(
                    "=================================================="
                )

                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "detail" to
                                "The account request body could not be read."
                    )
                )
            }

        if (rawRequestBody.isBlank()) {
            println(
                "Manager account creation request body is empty"
            )

            println(
                "=================================================="
            )

            return@post call.respond(
                HttpStatusCode.BadRequest,
                mapOf(
                    "detail" to
                            "The account request body is required."
                )
            )
        }

        println(
            "Incoming Manager account creation request:"
        )

        println(
            sanitizeManagerCreateUserBody(
                rawRequestBody
            )
        )

        val request =
            try {
                managerCreateUserJson
                    .decodeFromString<AccountCreateRequest>(
                        rawRequestBody
                    )
            } catch (exception: Exception) {
                println(
                    "Unable to convert request body to AccountCreateRequest"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
                )

                printManagerCreateUserExceptionCauses(
                    exception
                )

                println(
                    "=================================================="
                )

                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "detail" to
                                (
                                        exception.message
                                            ?: "The account request body is invalid."
                                        )
                    )
                )
            }

        println(
            "AccountCreateRequest decoded successfully"
        )

        println(
            "Requested user ID: ${request.userId}"
        )

        println(
            "Requested first name: ${request.firstName}"
        )

        println(
            "Requested middle name: ${request.middleName}"
        )

        println(
            "Requested last name: ${request.lastName}"
        )

        println(
            "Requested email: ${request.email}"
        )

        println(
            "Requested phone number: ${request.phoneNumber}"
        )

        println(
            "Requested role: ${request.role}"
        )

        println(
            "Requested gender: ${request.gender}"
        )

        println(
            "Requested region ID: ${request.regionId}"
        )

        println(
            "Requested district ID: ${request.districtId}"
        )

        println(
            "Requested directorate ID: ${request.directorateId}"
        )

        println(
            "Requested category ID: ${request.categoryId}"
        )

        println(
            "Requested current grade ID: ${request.currentGradeId}"
        )

        println(
            "Requested next grade ID: ${request.nextGradeId}"
        )

        println(
            "Requested management unit ID: ${request.managementUnitCostCentreId}"
        )

        println(
            "The backend will replace the requested region with the Manager region"
        )

        println(
            "The backend will set the created account role to Staff"
        )

        println(
            "The backend will apply the default password"
        )

        val result =
            try {
                dbQuery {
                    ManagerCreateUserService
                        .createUser(
                            managerAccountId =
                                principal.accountId,

                            request =
                                request
                        )
                }
            } catch (exception: Exception) {
                println(
                    "Unable to process Manager account creation request"
                )

                println(
                    "Authenticated account ID: ${principal.accountId}"
                )

                println(
                    "Requested user ID: ${request.userId}"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
                )

                printManagerCreateUserExceptionCauses(
                    exception
                )

                println(
                    "=================================================="
                )

                return@post call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                (
                                        exception.message
                                            ?: "The account could not be created."
                                        )
                    )
                )
            }

        when (result) {
            is ManagerCreateUserResult.Success -> {
                println(
                    "Manager-region account created successfully"
                )

                println(
                    "Created account ID: ${result.account.id}"
                )

                println(
                    "Created user ID: ${result.account.userId}"
                )

                println(
                    "Created full name: ${result.account.fullName}"
                )

                println(
                    "Created role: ${result.account.role}"
                )

                println(
                    "Created region ID: ${result.account.regionId}"
                )

                println(
                    "Created region name: ${result.account.regionName}"
                )

                println(
                    "Created account active: ${result.account.isActive}"
                )

                println(
                    "POST /api/manager/users/create completed with HTTP 201"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.Created,
                    result.account
                )
            }

            ManagerCreateUserResult.AccessDenied -> {
                println(
                    "Manager account creation access denied"
                )

                println(
                    "Reason: Authenticated account is not a Manager"
                )

                println(
                    "POST /api/manager/users/create completed with HTTP 403"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "Manager access is required."
                    )
                )
            }

            ManagerCreateUserResult.ManagerAccountNotFound -> {
                println(
                    "Authenticated Manager account was not found"
                )

                println(
                    "Authenticated account ID: ${principal.accountId}"
                )

                println(
                    "POST /api/manager/users/create completed with HTTP 404"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf(
                        "detail" to
                                "The authenticated Manager account was not found."
                    )
                )
            }

            ManagerCreateUserResult.ManagerAccountInactive -> {
                println(
                    "Inactive Manager attempted to create an account"
                )

                println(
                    "Authenticated account ID: ${principal.accountId}"
                )

                println(
                    "POST /api/manager/users/create completed with HTTP 403"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf(
                        "detail" to
                                "This Manager account is inactive."
                    )
                )
            }

            ManagerCreateUserResult.ManagerRegionNotAssigned -> {
                println(
                    "Manager does not have an assigned region"
                )

                println(
                    "Authenticated account ID: ${principal.accountId}"
                )

                println(
                    "POST /api/manager/users/create completed with HTTP 400"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "regionId" to
                                listOf(
                                    "A region has not been assigned to this Manager account."
                                )
                    )
                )
            }

            ManagerCreateUserResult.UserIdRequired -> {
                println(
                    "Manager account creation rejected"
                )

                println(
                    "Reason: User ID is required"
                )

                println(
                    "POST /api/manager/users/create completed with HTTP 400"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "userId" to
                                listOf(
                                    "User ID is required."
                                )
                    )
                )
            }

            ManagerCreateUserResult.UserIdExists -> {
                println(
                    "Manager account creation rejected"
                )

                println(
                    "Reason: User ID already exists"
                )

                println(
                    "Requested user ID: ${request.userId}"
                )

                println(
                    "POST /api/manager/users/create completed with HTTP 409"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.Conflict,
                    mapOf(
                        "userId" to
                                listOf(
                                    "An account with this user ID already exists."
                                )
                    )
                )
            }

            ManagerCreateUserResult.PhoneNumberExists -> {
                println(
                    "Manager account creation rejected"
                )

                println(
                    "Reason: Phone number already exists"
                )

                println(
                    "POST /api/manager/users/create completed with HTTP 409"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.Conflict,
                    mapOf(
                        "phoneNumber" to
                                listOf(
                                    "An account with this phone number already exists."
                                )
                    )
                )
            }

            ManagerCreateUserResult.CreatedAccountNotFound -> {
                println(
                    "Account was created but could not be retrieved"
                )

                println(
                    "Requested user ID: ${request.userId}"
                )

                println(
                    "POST /api/manager/users/create completed with HTTP 500"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "The account was created but could not be retrieved."
                    )
                )
            }

            ManagerCreateUserResult.Failed -> {
                println(
                    "Manager account creation service failed"
                )

                println(
                    "Authenticated account ID: ${principal.accountId}"
                )

                println(
                    "Requested user ID: ${request.userId}"
                )

                println(
                    "POST /api/manager/users/create completed with HTTP 500"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "detail" to
                                "The account could not be created."
                    )
                )
            }
        }
    }
}

private fun sanitizeManagerCreateUserBody(
    requestBody: String
): String {
    return try {
        val jsonElement =
            managerCreateUserJson
                .parseToJsonElement(
                    requestBody
                )

        val sanitizedElement =
            sanitizeManagerCreateUserElement(
                jsonElement
            )

        sanitizedElement.toString()
    } catch (exception: Exception) {
        println(
            "Unable to sanitize Manager account creation request"
        )

        println(
            "Error type: ${exception::class.simpleName}"
        )

        println(
            "Error message: ${exception.message}"
        )

        "[Account request received but could not be safely printed]"
    }
}

private fun sanitizeManagerCreateUserElement(
    jsonElement: JsonElement
): JsonElement {
    return when (jsonElement) {
        is JsonObject -> {
            val sanitizedContent =
                jsonElement.mapValues { entry ->
                    if (
                        isSensitiveManagerCreateUserField(
                            entry.key
                        )
                    ) {
                        JsonPrimitive(
                            "[HIDDEN]"
                        )
                    } else {
                        sanitizeManagerCreateUserElement(
                            entry.value
                        )
                    }
                }

            JsonObject(
                sanitizedContent
            )
        }

        is JsonArray -> {
            JsonArray(
                jsonElement.map { item ->
                    sanitizeManagerCreateUserElement(
                        item
                    )
                }
            )
        }

        else -> {
            jsonElement
        }
    }
}

private fun isSensitiveManagerCreateUserField(
    fieldName: String
): Boolean {
    val sensitiveFieldNames =
        setOf(
            "password",
            "passwordHash",
            "password_hash",
            "confirmPassword",
            "confirm_password",
            "ghanaCardNumber",
            "ghana_card_number",
            "socialSecurityNumber",
            "social_security_number",
            "nationalHealthInsuranceNumber",
            "national_health_insurance_number",
            "bankAccountNumber",
            "bank_account_number",
            "profilePicturePublicId",
            "profile_picture_public_id"
        )

    return fieldName in
            sensitiveFieldNames
}

private fun printManagerCreateUserExceptionCauses(
    exception: Exception
) {
    var currentCause =
        exception.cause

    var causeLevel =
        1

    while (currentCause != null) {
        println(
            "Cause $causeLevel type: ${currentCause::class.simpleName}"
        )

        println(
            "Cause $causeLevel message: ${currentCause.message}"
        )

        currentCause =
            currentCause.cause

        causeLevel +=
            1
    }
}