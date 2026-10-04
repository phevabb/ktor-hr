package com.hr.account.routes

import com.hr.account.dtos.AccountCreateRequest
import com.hr.account.dtos.AccountResponse
import com.hr.account.repositories.AccountRepository
import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.request.contentType
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put


import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive




private const val DEFAULT_ACCOUNT_PASSWORD =
    "Securepassword123!"

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
        println(
            "=================================================="
        )

        println(
            "POST /api/accounts request received"
        )

        val principal =
            call.principal<AuthPrincipal>()
                ?: return@post call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf(
                        "detail" to
                                "Authentication is required."
                    )
                )

        println(
            "Account creation requested by:"
        )

        println(
            "Authenticated account ID: ${principal.accountId}"
        )

        println(
            "Authenticated user ID: ${principal.userId}"
        )

        println(
            "Authenticated role: ${principal.role}"
        )



        val rawRequestBody =
            try {
                call.receiveText()
            } catch (exception: Exception) {
                println(
                    "Unable to read account request body"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
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
                "Account request body is empty"
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
            "Incoming sanitized account request:"
        )

        println(
            sanitizeAccountRequestBody(
                rawRequestBody
            )
        )

        val request =
            try {
                accountCreateJson
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

                exception.cause?.let { cause ->
                    println(
                        "Conversion cause type: ${cause::class.simpleName}"
                    )

                    println(
                        "Conversion cause message: ${cause.message}"
                    )
                }

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

        printDecodedAccountRequest(
            request
        )

        val normalizedUserId =
            request.userId.trim()

        if (normalizedUserId.isBlank()) {
            println(
                "Account validation failed: userId is blank"
            )

            return@post call.respond(
                HttpStatusCode.BadRequest,
                mapOf(
                    "userId" to
                            listOf(
                                "User ID is required."
                            )
                )
            )
        }


        if (
            request.standardRetirementAge <= 0
        ) {
            println(
                "Account validation failed: standardRetirementAge must be greater than zero"
            )

            println(
                "Received retirement age: ${request.standardRetirementAge}"
            )

            return@post call.respond(
                HttpStatusCode.BadRequest,
                mapOf(
                    "standardRetirementAge" to
                            listOf(
                                "Standard retirement age must be greater than zero."
                            )
                )
            )
        }

        println(
            "Initial account validation completed successfully"
        )

        val result =
            try {
                dbQuery {
                    println(
                        "Checking whether user ID already exists"
                    )

                    println(
                        "User ID being checked: $normalizedUserId"
                    )

                    val userIdExists =
                        AccountRepository
                            .userIdExists(
                                normalizedUserId
                            )

                    println(
                        "User ID already exists: $userIdExists"
                    )

                    if (userIdExists) {
                        return@dbQuery AccountOperationResult
                            .UserIdExists
                    }

                    val normalizedPhoneNumber =
                        request.phoneNumber
                            ?.trim()
                            ?.takeIf { phoneNumber ->
                                phoneNumber.isNotBlank()
                            }

                    if (
                        normalizedPhoneNumber != null
                    ) {
                        println(
                            "Checking whether phone number already exists"
                        )

                        val phoneNumberExists =
                            AccountRepository
                                .phoneNumberExists(
                                    normalizedPhoneNumber
                                )

                        println(
                            "Phone number already exists: $phoneNumberExists"
                        )

                        if (phoneNumberExists) {
                            return@dbQuery AccountOperationResult
                                .PhoneExists
                        }
                    } else {
                        println(
                            "No phone number was submitted"
                        )
                    }

                    println(
                        "Creating account in the database"
                    )

                    println(
                        "Creating account with user ID: $normalizedUserId"
                    )

                    println(
                        "Creating account with role: ${request.role}"
                    )

                    val accountId =
                        AccountRepository.create(
                            request
                        )

                    println(
                        "Account inserted successfully"
                    )

                    println(
                        "Created account database ID: $accountId"
                    )

                    println(
                        "Retrieving the newly created account"
                    )

                    val account =
                        AccountRepository
                            .getById(
                                accountId
                            )

                    if (account == null) {
                        println(
                            "Account was inserted but could not be retrieved"
                        )

                        println(
                            "Missing account ID: $accountId"
                        )

                        return@dbQuery AccountOperationResult
                            .Failed
                    }

                    println(
                        "New account retrieved successfully"
                    )

                    println(
                        "Created account ID: ${account.id}"
                    )

                    println(
                        "Created account user ID: ${account.userId}"
                    )

                    println(
                        "Created account full name: ${account.fullName}"
                    )

                    println(
                        "Created account role: ${account.role}"
                    )

                    AccountOperationResult.Success(
                        account
                    )
                }
            } catch (exception: Exception) {
                println(
                    "Account creation database operation failed"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
                )

                exception.cause?.let { cause ->
                    println(
                        "Database error cause type: ${cause::class.simpleName}"
                    )

                    println(
                        "Database error cause message: ${cause.message}"
                    )
                }

                exception.printStackTrace()

                println(
                    "POST /api/accounts completed with HTTP 500"
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
            AccountOperationResult.UserIdExists -> {
                println(
                    "Account creation rejected because the user ID already exists"
                )

                println(
                    "Duplicate user ID: $normalizedUserId"
                )

                println(
                    "POST /api/accounts completed with HTTP 409"
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

            AccountOperationResult.PhoneExists -> {
                println(
                    "Account creation rejected because the phone number already exists"
                )

                println(
                    "POST /api/accounts completed with HTTP 409"
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

            AccountOperationResult.Failed -> {
                println(
                    "Account creation failed after the database insertion"
                )

                println(
                    "POST /api/accounts completed with HTTP 500"
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

            AccountOperationResult.NotFound -> {
                println(
                    "Account creation returned an account-not-found result"
                )

                println(
                    "POST /api/accounts completed with HTTP 404"
                )

                println(
                    "=================================================="
                )

                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf(
                        "detail" to
                                "Account not found."
                    )
                )
            }

            is AccountOperationResult.Success -> {
                println(
                    "Account created successfully"
                )

                println(
                    "Account ID: ${result.account.id}"
                )

                println(
                    "User ID: ${result.account.userId}"
                )

                println(
                    "Full name: ${result.account.fullName}"
                )

                println(
                    "Role: ${result.account.role}"
                )

                println(
                    "Gender: ${result.account.gender}"
                )

                println(
                    "Region: ${result.account.regionName}"
                )

                println(
                    "District: ${result.account.districtName}"
                )

                println(
                    "Directorate: ${result.account.directorateName}"
                )

                println(
                    "Class: ${result.account.categoryName}"
                )

                println(
                    "Current grade: ${result.account.currentGradeName}"
                )

                println(
                    "Management unit: ${result.account.managementUnitCostCentreName}"
                )

                println(
                    "Account active: ${result.account.isActive}"
                )

                println(
                    "POST /api/accounts completed with HTTP 201"
                )

                println(
                    "=================================================="
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
            AccountRepository.getById(id)
                ?: return@dbQuery AccountOperationResult.NotFound

            val normalizedUserId =
                request.userId.trim()

            if (
                AccountRepository.userIdExists(
                    userId = normalizedUserId,
                    excludeAccountId = id
                )
            ) {
                return@dbQuery AccountOperationResult.UserIdExists
            }

            val normalizedPhoneNumber =
                request.phoneNumber
                    ?.trim()
                    ?.takeIf {
                        it.isNotBlank()
                    }

            if (
                normalizedPhoneNumber != null &&
                AccountRepository.phoneNumberExists(
                    phoneNumber = normalizedPhoneNumber,
                    excludeAccountId = id
                )
            ) {
                return@dbQuery AccountOperationResult.PhoneExists
            }

            val updated =
                AccountRepository.update(
                    id = id,
                    request = request.copy(
                        userId = normalizedUserId,
                        phoneNumber = normalizedPhoneNumber
                    )
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








































private val accountCreateJson =
    Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
        coerceInputValues = false
        prettyPrint = true
    }


private fun printDecodedAccountRequest(
    request: AccountCreateRequest
) {
    println(
        "Decoded account request values:"
    )

    println(
        "userId=${request.userId}"
    )

    println(
        "password=[HIDDEN]"
    )

    println(
        "role=${request.role}"
    )

    println(
        "firstName=${request.firstName}"
    )

    println(
        "middleName=${request.middleName}"
    )

    println(
        "lastName=${request.lastName}"
    )

    println(
        "maidenName=${request.maidenName}"
    )

    println(
        "email=${request.email}"
    )

    println(
        "phoneNumber=${request.phoneNumber}"
    )

    println(
        "gender=${request.gender}"
    )

    println(
        "maritalStatus=${request.maritalStatus}"
    )

    println(
        "academicQualificationId=${request.academicQualificationId}"
    )

    println(
        "directorateId=${request.directorateId}"
    )

    println(
        "categoryId=${request.categoryId}"
    )

    println(
        "districtId=${request.districtId}"
    )

    println(
        "regionId=${request.regionId}"
    )

    println(
        "currentGradeId=${request.currentGradeId}"
    )

    println(
        "nextGradeId=${request.nextGradeId}"
    )

    println(
        "changeOfGradeId=${request.changeOfGradeId}"
    )

    println(
        "managementUnitCostCentreId=${request.managementUnitCostCentreId}"
    )

    println(
        "titleId=${request.titleId}"
    )

    println(
        "onLeaveTypeId=${request.onLeaveTypeId}"
    )

    println(
        "professional=${request.professional}"
    )

    println(
        "professionalQualification=${request.professionalQualification}"
    )

    println(
        "staffCategory=${request.staffCategory}"
    )

    println(
        "fulltimeContractStaff=${request.fulltimeContractStaff}"
    )

    println(
        "dateOfBirth=${request.dateOfBirth}"
    )

    println(
        "standardRetirementAge=${request.standardRetirementAge}"
    )

    println(
        "dateOfAssumptionOfDuty=${request.dateOfAssumptionOfDuty}"
    )

    println(
        "substantiveDate=${request.substantiveDate}"
    )

    println(
        "nationalEffectiveDate=${request.nationalEffectiveDate}"
    )

    println(
        "dateOfLastPromotion=${request.dateOfLastPromotion}"
    )

    println(
        "dateOfFirstAppointment=${request.dateOfFirstAppointment}"
    )

    println(
        "currentSalaryLevel=${request.currentSalaryLevel}"
    )

    println(
        "currentSalaryPoint=${request.currentSalaryPoint}"
    )

    println(
        "nextSalaryLevel=${request.nextSalaryLevel}"
    )

    println(
        "singleSpineMonthlySalary=${request.singleSpineMonthlySalary}"
    )

    println(
        "monthlyGrossPay=${request.monthlyGrossPay}"
    )

    println(
        "annualSalary=${request.annualSalary}"
    )

    println(
        "numberOfFocusAreas=${request.numberOfFocusAreas}"
    )

    println(
        "numberOfTargets=${request.numberOfTargets}"
    )

    println(
        "numberOfTargetsMet=${request.numberOfTargetsMet}"
    )

    println(
        "numberOfTargetsNotMet=${request.numberOfTargetsNotMet}"
    )

    println(
        "overallAssessmentScore=${request.overallAssessmentScore}"
    )

    println(
        "selfAssessmentDescription=${request.selfAssessmentDescription}"
    )

    println(
        "ghanaCardNumber=[HIDDEN]"
    )

    println(
        "socialSecurityNumber=[HIDDEN]"
    )

    println(
        "nationalHealthInsuranceNumber=[HIDDEN]"
    )

    println(
        "bankName=${request.bankName}"
    )

    println(
        "bankAccountBranch=${request.bankAccountBranch}"
    )

    println(
        "bankAccountNumber=[HIDDEN]"
    )

    println(
        "payrollStatus=${request.payrollStatus}"
    )

    println(
        "atPostOnLeave=${request.atPostOnLeave}"
    )

    println(
        "accommodationStatus=${request.accommodationStatus}"
    )

    println(
        "supervisorName=${request.supervisorName}"
    )

    println(
        "isActive=${request.isActive}"
    )

    println(
        "isStaff=${request.isStaff}"
    )

    println(
        "isSuperuser=${request.isSuperuser}"
    )
}

private fun sanitizeAccountRequestBody(
    requestBody: String
): String {
    return try {
        val jsonElement =
            accountCreateJson
                .parseToJsonElement(
                    requestBody
                )

        val sanitizedJson =
            sanitizeJsonElement(
                jsonElement
            )

        sanitizedJson.toString()
    } catch (exception: Exception) {
        println(
            "Unable to sanitize account request body"
        )

        println(
            "Error type: ${exception::class.simpleName}"
        )

        println(
            "Error message: ${exception.message}"
        )

        "[Account request body received but could not be safely printed]"
    }
}

private fun sanitizeJsonElement(
    jsonElement: JsonElement
): JsonElement {
    return when (jsonElement) {
        is JsonObject -> {
            val sanitizedContent =
                jsonElement.mapValues { entry ->
                    if (
                        isSensitiveAccountField(
                            entry.key
                        )
                    ) {
                        JsonPrimitive(
                            "[HIDDEN]"
                        )
                    } else {
                        sanitizeJsonElement(
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
                    sanitizeJsonElement(
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

private fun isSensitiveAccountField(
    fieldName: String
): Boolean {
    val sensitiveFieldNames =
        setOf(
            "password",
            "passwordHash",
            "password_hash",
            "bankAccountNumber",
            "bank_account_number",
            "ghanaCardNumber",
            "ghana_card_number",
            "socialSecurityNumber",
            "social_security_number",
            "nationalHealthInsuranceNumber",
            "national_health_insurance_number",
            "profilePicturePublicId",
            "profile_picture_public_id"
        )

    return fieldName in
            sensitiveFieldNames
}