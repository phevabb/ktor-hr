package com.hr.account.services

import com.hr.account.dtos.AccommodationStatus
import com.hr.account.dtos.AccountCreateMultipartData
import com.hr.account.dtos.AccountCreateRequest
import com.hr.account.dtos.AccountProfilePictureUpload
import com.hr.account.dtos.AtPostOnLeave
import com.hr.account.dtos.FulltimeContractStaff
import com.hr.account.dtos.Gender
import com.hr.account.dtos.MaritalStatus
import com.hr.account.dtos.PayrollStatus
import com.hr.account.dtos.Professional
import com.hr.account.dtos.Role
import com.hr.account.dtos.SalaryLevel
import com.hr.account.dtos.SalaryPoint
import com.hr.account.dtos.StaffCategory
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveMultipart
import io.ktor.utils.io.jvm.javaio.toInputStream

object AccountCreateMultipartParser {





    private inline fun <
            reified T : Enum<T>
            > requiredEnum(
        values: Map<String, List<String>>,
        fieldName: String
    ): T {
        val rawValue =
            text(
                values,
                fieldName
            )
                .trim()

        if (rawValue.isBlank()) {
            throw IllegalArgumentException(
                "$fieldName is required."
            )
        }

        return findEnumValue<T>(
            rawValue
        )
            ?: throw IllegalArgumentException(
                "Invalid $fieldName value: $rawValue"
            )
    }

    private inline fun <
            reified T : Enum<T>
            > optionalEnum(
        values: Map<String, List<String>>,
        fieldName: String
    ): T? {
        val rawValue =
            nullableText(
                values,
                fieldName
            )
                ?: return null

        return findEnumValue<T>(
            rawValue
        )
            ?: throw IllegalArgumentException(
                "Invalid $fieldName value: $rawValue"
            )
    }

    private inline fun <
            reified T : Enum<T>
            > findEnumValue(
        rawValue: String
    ): T? {
        val normalizedValue =
            rawValue
                .trim()
                .replace(
                    oldChar = ' ',
                    newChar = '_'
                )
                .replace(
                    oldChar = '-',
                    newChar = '_'
                )

        return enumValues<T>()
            .firstOrNull { enumValue ->
                enumValue.name.equals(
                    other =
                        normalizedValue,
                    ignoreCase =
                        true
                ) ||
                        enumValue.toString()
                            .equals(
                                other =
                                    rawValue.trim(),
                                ignoreCase =
                                    true
                            )
            }
    }


    private inline fun <
            reified T : Enum<T>
            > parseRequiredEnum(
        value: String,
        fieldName: String
    ): T {
        val normalizedValue =
            value
                .trim()
                .replace(
                    oldChar =
                        ' ',

                    newChar =
                        '_'
                )
                .replace(
                    oldChar =
                        '-',

                    newChar =
                        '_'
                )

        if (normalizedValue.isBlank()) {
            throw IllegalArgumentException(
                "$fieldName is required."
            )
        }

        return enumValues<T>()
            .firstOrNull { enumValue ->
                enumValue.name.equals(
                    other =
                        normalizedValue,

                    ignoreCase =
                        true
                ) ||
                        enumValue.toString()
                            .equals(
                                other =
                                    value.trim(),

                                ignoreCase =
                                    true
                            )
            }
            ?: throw IllegalArgumentException(
                "Invalid $fieldName value: $value"
            )
    }



    private inline fun <
            reified T : Enum<T>
            > parseOptionalEnum(
        value: String?,
        fieldName: String
    ): T? {
        val rawValue =
            value
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: return null

        return findEnumValue<T>(
            rawValue
        )
            ?: throw IllegalArgumentException(
                "Invalid $fieldName value: $rawValue"
            )
    }




    suspend fun parse(
        call: ApplicationCall
    ): AccountCreateMultipartData {
        val values =
            linkedMapOf<String, MutableList<String>>()

        var profilePicture:
                AccountProfilePictureUpload? =
            null

        val multipart =
            call.receiveMultipart()

        multipart.forEachPart { part ->
            try {
                when (part) {
                    is PartData.FormItem -> {
                        val fieldName =
                            normalizeFieldName(
                                part.name
                            )

                        if (fieldName.isNotBlank()) {
                            values
                                .getOrPut(
                                    fieldName
                                ) {
                                    mutableListOf()
                                }
                                .add(
                                    part.value.trim()
                                )
                        }
                    }

                    is PartData.FileItem -> {
                        val fieldName =
                            normalizeFieldName(
                                part.name
                            )

                        if (
                            fieldName ==
                            "profilePictureUrl"
                        ) {
                            val bytes =
                                part.provider()
                                    .toInputStream()
                                    .use { inputStream ->
                                        inputStream.readBytes()
                                    }

                            if (bytes.isNotEmpty()) {
                                profilePicture =
                                    AccountProfilePictureUpload(
                                        originalFileName =
                                            part.originalFileName
                                                ?.trim()
                                                ?.takeIf {
                                                    it.isNotBlank()
                                                },

                                        contentType =
                                            part.contentType
                                                ?.toString(),

                                        bytes =
                                            bytes
                                    )
                            }
                        }
                    }

                    else -> {
                        println(
                            "Unsupported multipart part ignored"
                        )
                    }
                }
            } finally {
                part.dispose()
            }
        }

        println(
            "Account-create multipart request parsed"
        )

        println(
            "Received text fields: ${values.keys}"
        )

        println(
            "Profile picture included: ${profilePicture != null}"
        )

        val request =
            AccountCreateRequest(
                userId =
                    text(
                        values,
                        "userId"
                    ),

                role =
                    parseRequiredEnum<Role>(
                        value =
                            text(
                                values,
                                "role"
                            ),

                        fieldName =
                            "role"
                    ),

                firstName =
                    nullableText(
                        values,
                        "firstName"
                    ),

                middleName =
                    nullableText(
                        values,
                        "middleName"
                    ),

                lastName =
                    nullableText(
                        values,
                        "lastName"
                    ),

                maidenName =
                    nullableText(
                        values,
                        "maidenName"
                    ),

                email =
                    nullableText(
                        values,
                        "email"
                    ),

                gender =
                    parseOptionalEnum<Gender>(
                        value =
                            nullableText(
                                values,
                                "gender"
                            ),

                        fieldName =
                            "gender"
                    ),

                dateOfBirth =
                    nullableText(
                        values,
                        "dateOfBirth"
                    ),

                standardRetirementAge =
                    integer(
                        values,
                        "standardRetirementAge"
                    )
                        ?: 60,

                maritalStatus =
                    parseOptionalEnum<MaritalStatus>(
                        value =
                            nullableText(
                                values,
                                "maritalStatus"
                            ),

                        fieldName =
                            "maritalStatus"
                    ),

                academicQualificationId =
                    integer(
                        values,
                        "academicQualificationId"
                    ),

                directorateId =
                    integer(
                        values,
                        "directorateId"
                    ),

                categoryId =
                    integer(
                        values,
                        "categoryId"
                    ),

                districtId =
                    integer(
                        values,
                        "districtId"
                    ),

                regionId =
                    integer(
                        values,
                        "regionId"
                    ),

                currentGradeId =
                    integer(
                        values,
                        "currentGradeId"
                    ),

                nextGradeId =
                    integer(
                        values,
                        "nextGradeId"
                    ),

                changeOfGradeId =
                    integer(
                        values,
                        "changeOfGradeId"
                    ),

                managementUnitCostCentreId =
                    integer(
                        values,
                        "managementUnitCostCentreId"
                    ),

                titleId =
                    integer(
                        values,
                        "titleId"
                    ),

                onLeaveTypeId =
                    integer(
                        values,
                        "onLeaveTypeId"
                    ),

                professional =
                    parseOptionalEnum<Professional>(
                        value =
                            nullableText(
                                values,
                                "professional"
                            ),

                        fieldName =
                            "professional"
                    ),

                professionalQualification =
                    nullableText(
                        values,
                        "professionalQualification"
                    ),

                staffCategory =
                    parseOptionalEnum<StaffCategory>(
                        value =
                            nullableText(
                                values,
                                "staffCategory"
                            ),

                        fieldName =
                            "staffCategory"
                    ),

                fulltimeContractStaff =
                    parseOptionalEnum<FulltimeContractStaff>(
                        value =
                            nullableText(
                                values,
                                "fulltimeContractStaff"
                            ),

                        fieldName =
                            "fulltimeContractStaff"
                    ),

                atPostOnLeave =
                    parseOptionalEnum<AtPostOnLeave>(
                        value =
                            nullableText(
                                values,
                                "atPostOnLeave"
                            ),

                        fieldName =
                            "atPostOnLeave"
                    ),

                currentSalaryLevel =
                    parseOptionalEnum<SalaryLevel>(
                        value =
                            nullableText(
                                values,
                                "currentSalaryLevel"
                            ),

                        fieldName =
                            "currentSalaryLevel"
                    ),

                currentSalaryPoint =
                    parseOptionalEnum<SalaryPoint>(
                        value =
                            nullableText(
                                values,
                                "currentSalaryPoint"
                            ),

                        fieldName =
                            "currentSalaryPoint"
                    ),

                nextSalaryLevel =
                    parseOptionalEnum<SalaryLevel>(
                        value =
                            nullableText(
                                values,
                                "nextSalaryLevel"
                            ),

                        fieldName =
                            "nextSalaryLevel"
                    ),

                dateOfAssumptionOfDuty =
                    nullableText(
                        values,
                        "dateOfAssumptionOfDuty"
                    ),

                substantiveDate =
                    nullableText(
                        values,
                        "substantiveDate"
                    ),

                nationalEffectiveDate =
                    nullableText(
                        values,
                        "nationalEffectiveDate"
                    ),

                dateOfLastPromotion =
                    nullableText(
                        values,
                        "dateOfLastPromotion"
                    ),

                dateOfFirstAppointment =
                    nullableText(
                        values,
                        "dateOfFirstAppointment"
                    ),

                singleSpineMonthlySalary =
                    nullableText(
                        values,
                        "singleSpineMonthlySalary"
                    ),

                monthlyGrossPay =
                    nullableText(
                        values,
                        "monthlyGrossPay"
                    ),

                annualSalary =
                    nullableText(
                        values,
                        "annualSalary"
                    ),

                numberOfFocusAreas =
                    integer(
                        values,
                        "numberOfFocusAreas"
                    ),

                numberOfTargets =
                    integer(
                        values,
                        "numberOfTargets"
                    ),

                numberOfTargetsMet =
                    integer(
                        values,
                        "numberOfTargetsMet"
                    ),

                numberOfTargetsNotMet =
                    integer(
                        values,
                        "numberOfTargetsNotMet"
                    ),

                overallAssessmentScore =
                    nullableText(
                        values,
                        "overallAssessmentScore"
                    ),

                selfAssessmentDescription =
                    nullableText(
                        values,
                        "selfAssessmentDescription"
                    ),

                phoneNumber =
                    nullableText(
                        values,
                        "phoneNumber"
                    ),

                ghanaCardNumber =
                    nullableText(
                        values,
                        "ghanaCardNumber"
                    ),

                socialSecurityNumber =
                    nullableText(
                        values,
                        "socialSecurityNumber"
                    ),

                nationalHealthInsuranceNumber =
                    nullableText(
                        values,
                        "nationalHealthInsuranceNumber"
                    ),

                bankName =
                    nullableText(
                        values,
                        "bankName"
                    ),

                bankAccountNumber =
                    nullableText(
                        values,
                        "bankAccountNumber"
                    ),

                bankAccountBranch =
                    nullableText(
                        values,
                        "bankAccountBranch"
                    ),

                payrollStatus =
                    parseOptionalEnum<PayrollStatus>(
                        value =
                            nullableText(
                                values,
                                "payrollStatus"
                            ),

                        fieldName =
                            "payrollStatus"
                    ),

                accommodationStatus =
                    parseOptionalEnum<AccommodationStatus>(
                        value =
                            nullableText(
                                values,
                                "accommodationStatus"
                            ),

                        fieldName =
                            "accommodationStatus"
                    ),

                supervisorName =
                    nullableText(
                        values,
                        "supervisorName"
                    ),

                isActive =
                    boolean(
                        values,
                        "isActive",
                        defaultValue =
                            true
                    ),

                isStaff =
                    boolean(
                        values,
                        "isStaff",
                        defaultValue =
                            false
                    ),

                isSuperuser =
                    boolean(
                        values,
                        "isSuperuser",
                        defaultValue =
                            false
                    ),

                profilePictureUrl =
                    null,

                profilePicturePublicId =
                    null
            )



        return AccountCreateMultipartData(
            request =
                request,

            profilePicture =
                profilePicture
        )
    }

    private fun normalizeFieldName(
        fieldName: String?
    ): String {
        return when (
            fieldName
                ?.trim()
                .orEmpty()
        ) {
            "user_id" ->
                "userId"

            "first_name" ->
                "firstName"

            "middle_name" ->
                "middleName"

            "last_name" ->
                "lastName"

            "maiden_name" ->
                "maidenName"

            "date_of_birth" ->
                "dateOfBirth"

            "standard_retirement_age" ->
                "standardRetirementAge"

            "marital_status" ->
                "maritalStatus"

            "academic_qualification_id" ->
                "academicQualificationId"

            "directorate_id" ->
                "directorateId"

            "category_id" ->
                "categoryId"

            "district_id" ->
                "districtId"

            "region_id" ->
                "regionId"

            "current_grade_id" ->
                "currentGradeId"

            "next_grade_id" ->
                "nextGradeId"

            "change_of_grade_id" ->
                "changeOfGradeId"

            "management_unit_cost_centre_id" ->
                "managementUnitCostCentreId"

            "title_id" ->
                "titleId"

            "on_leave_type_id" ->
                "onLeaveTypeId"

            "professional_qualification" ->
                "professionalQualification"

            "staff_category" ->
                "staffCategory"

            "fulltime_contract_staff" ->
                "fulltimeContractStaff"

            "at_post_on_leave" ->
                "atPostOnLeave"

            "current_salary_level" ->
                "currentSalaryLevel"

            "current_salary_point" ->
                "currentSalaryPoint"

            "next_salary_level" ->
                "nextSalaryLevel"

            "date_of_assumption_of_duty" ->
                "dateOfAssumptionOfDuty"

            "substantive_date" ->
                "substantiveDate"

            "national_effective_date" ->
                "nationalEffectiveDate"

            "date_of_last_promotion" ->
                "dateOfLastPromotion"

            "date_of_first_appointment" ->
                "dateOfFirstAppointment"

            "single_spine_monthly_salary" ->
                "singleSpineMonthlySalary"

            "monthly_gross_pay" ->
                "monthlyGrossPay"

            "annual_salary" ->
                "annualSalary"

            "number_of_focus_areas" ->
                "numberOfFocusAreas"

            "number_of_targets" ->
                "numberOfTargets"

            "number_of_targets_met" ->
                "numberOfTargetsMet"

            "number_of_targets_not_met" ->
                "numberOfTargetsNotMet"

            "overall_assessment_score" ->
                "overallAssessmentScore"

            "self_assessment_description" ->
                "selfAssessmentDescription"

            "phone_number" ->
                "phoneNumber"

            "ghana_card_number" ->
                "ghanaCardNumber"

            "social_security_number" ->
                "socialSecurityNumber"

            "national_health_insurance_number" ->
                "nationalHealthInsuranceNumber"

            "bank_name" ->
                "bankName"

            "bank_account_number" ->
                "bankAccountNumber"

            "bank_account_branch" ->
                "bankAccountBranch"

            "payroll_status" ->
                "payrollStatus"

            "accommodation_status" ->
                "accommodationStatus"

            "supervisor_name" ->
                "supervisorName"

            "is_active" ->
                "isActive"

            "is_staff" ->
                "isStaff"

            "is_superuser" ->
                "isSuperuser"

            "profile_picture",
            "profile_picture_url" ->
                "profilePictureUrl"

            "profile_picture_public_id" ->
                "profilePicturePublicId"

            else ->
                fieldName
                    ?.trim()
                    .orEmpty()
        }
    }

    private fun text(
        values: Map<String, List<String>>,
        fieldName: String
    ): String {
        return values[
            fieldName
        ]
            ?.firstOrNull()
            ?.trim()
            .orEmpty()
    }

    private fun nullableText(
        values: Map<String, List<String>>,
        fieldName: String
    ): String? {
        return text(
            values,
            fieldName
        )
            .takeIf {
                it.isNotBlank()
            }
    }

    private fun integer(
        values: Map<String, List<String>>,
        fieldName: String
    ): Int? {
        return text(
            values,
            fieldName
        )
            .toIntOrNull()
    }

    private fun boolean(
        values: Map<String, List<String>>,
        fieldName: String,
        defaultValue: Boolean
    ): Boolean {
        val value =
            text(
                values,
                fieldName
            )
                .lowercase()

        if (value.isBlank()) {
            return defaultValue
        }

        return when (value) {
            "true",
            "1",
            "yes",
            "on" ->
                true

            "false",
            "0",
            "no",
            "off" ->
                false

            else ->
                defaultValue
        }
    }
}