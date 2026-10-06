package com.hr.admin.services

import com.hr.admin.dtos.AdminProfilePictureUpload
import com.hr.admin.dtos.AdminUserUpdateRequest
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.request.receiveMultipart
import io.ktor.utils.io.toByteArray

data class AdminUserUpdateMultipartData(
    val request:
    AdminUserUpdateRequest,

    val profilePicture:
    AdminProfilePictureUpload?
)

object AdminUserUpdateMultipartParser {

    suspend fun parse(
        call: io.ktor.server.application.ApplicationCall
    ): AdminUserUpdateMultipartData {
        val textValues =
            linkedMapOf<String, MutableList<String>>()

        var profilePicture:
                AdminProfilePictureUpload? =
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
                            textValues
                                .getOrPut(
                                    fieldName
                                ) {
                                    mutableListOf()
                                }
                                .add(
                                    part.value
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
                                    .toByteArray()

                            if (bytes.isNotEmpty()) {
                                profilePicture =
                                    AdminProfilePictureUpload(
                                        originalFileName =
                                            part.originalFileName
                                                ?: "profile-image",

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
                        // No action required.
                    }
                }
            } finally {
                part.dispose()
            }
        }

        val providedFields =
            textValues.keys
                .toMutableSet()

        if (profilePicture != null) {
            providedFields.add(
                "profilePictureUrl"
            )
        }

        val academicQualificationId =
            getFirstInteger(
                values =
                    textValues,

                names =
                    listOf(
                        "academicQualificationId",
                        "academicQualifications"
                    )
            )

        if (
            textValues.containsKey(
                "academicQualifications"
            )
        ) {
            providedFields.add(
                "academicQualificationId"
            )
        }

        val request =
            AdminUserUpdateRequest(
                providedFields =
                    providedFields,

                userId =
                    getFirst(
                        textValues,
                        "userId"
                    ),

                email =
                    getFirst(
                        textValues,
                        "email"
                    ),

                firstName =
                    getFirst(
                        textValues,
                        "firstName"
                    ),

                middleName =
                    getFirst(
                        textValues,
                        "middleName"
                    ),

                lastName =
                    getFirst(
                        textValues,
                        "lastName"
                    ),

                maidenName =
                    getFirst(
                        textValues,
                        "maidenName"
                    ),

                role =
                    getFirst(
                        textValues,
                        "role"
                    ),

                professional =
                    getFirst(
                        textValues,
                        "professional"
                    ),

                gender =
                    getFirst(
                        textValues,
                        "gender"
                    ),

                maritalStatus =
                    getFirst(
                        textValues,
                        "maritalStatus"
                    ),

                staffCategory =
                    getFirst(
                        textValues,
                        "staffCategory"
                    ),

                fulltimeContractStaff =
                    getFirst(
                        textValues,
                        "fulltimeContractStaff"
                    ),

                atPostOnLeave =
                    getFirst(
                        textValues,
                        "atPostOnLeave"
                    ),

                payrollStatus =
                    getFirst(
                        textValues,
                        "payrollStatus"
                    ),

                accommodationStatus =
                    getFirst(
                        textValues,
                        "accommodationStatus"
                    ),

                currentSalaryLevel =
                    getFirst(
                        textValues,
                        "currentSalaryLevel"
                    ),

                currentSalaryPoint =
                    getFirst(
                        textValues,
                        "currentSalaryPoint"
                    ),

                nextSalaryLevel =
                    getFirst(
                        textValues,
                        "nextSalaryLevel"
                    ),

                currentGradeId =
                    getInteger(
                        textValues,
                        "currentGradeId"
                    ),

                nextGradeId =
                    getInteger(
                        textValues,
                        "nextGradeId"
                    ),

                changeOfGradeId =
                    getInteger(
                        textValues,
                        "changeOfGradeId"
                    ),

                managementUnitCostCentreId =
                    getInteger(
                        textValues,
                        "managementUnitCostCentreId"
                    ),

                directorateId =
                    getInteger(
                        textValues,
                        "directorateId"
                    ),

                categoryId =
                    getInteger(
                        textValues,
                        "categoryId"
                    ),

                districtId =
                    getInteger(
                        textValues,
                        "districtId"
                    ),

                regionId =
                    getInteger(
                        textValues,
                        "regionId"
                    ),

                titleId =
                    getInteger(
                        textValues,
                        "titleId"
                    ),

                onLeaveTypeId =
                    getInteger(
                        textValues,
                        "onLeaveTypeId"
                    ),

                academicQualificationId =
                    academicQualificationId,

                dateOfBirth =
                    getFirst(
                        textValues,
                        "dateOfBirth"
                    ),

                dateOfAssumptionOfDuty =
                    getFirst(
                        textValues,
                        "dateOfAssumptionOfDuty"
                    ),

                substantiveDate =
                    getFirst(
                        textValues,
                        "substantiveDate"
                    ),

                nationalEffectiveDate =
                    getFirst(
                        textValues,
                        "nationalEffectiveDate"
                    ),

                dateOfFirstAppointment =
                    getFirst(
                        textValues,
                        "dateOfFirstAppointment"
                    ),

                dateOfLastPromotion =
                    getFirst(
                        textValues,
                        "dateOfLastPromotion"
                    ),

                professionalQualification =
                    getFirst(
                        textValues,
                        "professionalQualification"
                    ),

                phoneNumber =
                    getFirst(
                        textValues,
                        "phoneNumber"
                    ),

                ghanaCardNumber =
                    getFirst(
                        textValues,
                        "ghanaCardNumber"
                    ),

                socialSecurityNumber =
                    getFirst(
                        textValues,
                        "socialSecurityNumber"
                    ),

                nationalHealthInsuranceNumber =
                    getFirst(
                        textValues,
                        "nationalHealthInsuranceNumber"
                    ),

                bankName =
                    getFirst(
                        textValues,
                        "bankName"
                    ),

                bankAccountBranch =
                    getFirst(
                        textValues,
                        "bankAccountBranch"
                    ),

                bankAccountNumber =
                    getFirst(
                        textValues,
                        "bankAccountNumber"
                    ),

                supervisorName =
                    getFirst(
                        textValues,
                        "supervisorName"
                    ),

                selfAssessmentDescription =
                    getFirst(
                        textValues,
                        "selfAssessmentDescription"
                    ),

                standardRetirementAge =
                    getInteger(
                        textValues,
                        "standardRetirementAge"
                    ),

                numberOfTargets =
                    getInteger(
                        textValues,
                        "numberOfTargets"
                    ),

                numberOfTargetsMet =
                    getInteger(
                        textValues,
                        "numberOfTargetsMet"
                    ),

                numberOfTargetsNotMet =
                    getInteger(
                        textValues,
                        "numberOfTargetsNotMet"
                    ),

                numberOfFocusAreas =
                    getInteger(
                        textValues,
                        "numberOfFocusAreas"
                    ),

                singleSpineMonthlySalary =
                    getFirst(
                        textValues,
                        "singleSpineMonthlySalary"
                    ),

                monthlyGrossPay =
                    getFirst(
                        textValues,
                        "monthlyGrossPay"
                    ),

                annualSalary =
                    getFirst(
                        textValues,
                        "annualSalary"
                    ),

                overallAssessmentScore =
                    getFirst(
                        textValues,
                        "overallAssessmentScore"
                    ),

                removeProfilePicture =
                    getBoolean(
                        textValues,
                        "removeProfilePicture"
                    )
            )

        return AdminUserUpdateMultipartData(
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

            "marital_status" ->
                "maritalStatus"

            "staff_category" ->
                "staffCategory"

            "fulltime_contract_staff" ->
                "fulltimeContractStaff"

            "at_post_on_leave" ->
                "atPostOnLeave"

            "payroll_status" ->
                "payrollStatus"

            "accommodation_status" ->
                "accommodationStatus"

            "current_salary_level" ->
                "currentSalaryLevel"

            "current_salary_point" ->
                "currentSalaryPoint"

            "next_salary_level" ->
                "nextSalaryLevel"

            "current_grade",
            "current_grade_id" ->
                "currentGradeId"

            "next_grade",
            "next_grade_id" ->
                "nextGradeId"

            "change_of_grade",
            "change_of_grade_id" ->
                "changeOfGradeId"

            "management_unit_cost_centre",
            "management_unit_cost_centre_id" ->
                "managementUnitCostCentreId"

            "directorate",
            "directorate_id" ->
                "directorateId"

            "category",
            "category_id" ->
                "categoryId"

            "district",
            "district_id" ->
                "districtId"

            "region",
            "region_id" ->
                "regionId"

            "title",
            "title_id" ->
                "titleId"

            "on_leave_type",
            "on_leave_type_id" ->
                "onLeaveTypeId"

            "academic_qualification_id" ->
                "academicQualificationId"

            "academic_qualifications" ->
                "academicQualifications"

            "date_of_birth" ->
                "dateOfBirth"

            "date_of_assumption_of_duty" ->
                "dateOfAssumptionOfDuty"

            "substantive_date" ->
                "substantiveDate"

            "national_effective_date" ->
                "nationalEffectiveDate"

            "date_of_first_appointment" ->
                "dateOfFirstAppointment"

            "date_of_last_promotion" ->
                "dateOfLastPromotion"

            "professional_qualification" ->
                "professionalQualification"

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

            "bank_account_branch" ->
                "bankAccountBranch"

            "bank_account_number" ->
                "bankAccountNumber"

            "supervisor_name" ->
                "supervisorName"

            "self_assessment_description" ->
                "selfAssessmentDescription"

            "standard_retirement_age" ->
                "standardRetirementAge"

            "number_of_targets" ->
                "numberOfTargets"

            "number_of_targets_met" ->
                "numberOfTargetsMet"

            "number_of_targets_not_met" ->
                "numberOfTargetsNotMet"

            "number_of_focus_areas" ->
                "numberOfFocusAreas"

            "single_spine_monthly_salary" ->
                "singleSpineMonthlySalary"

            "monthly_gross_pay" ->
                "monthlyGrossPay"

            "annual_salary" ->
                "annualSalary"

            "overall_assessment_score" ->
                "overallAssessmentScore"

            "profile_picture",
            "profile_picture_url" ->
                "profilePictureUrl"

            "remove_profile_picture" ->
                "removeProfilePicture"

            else ->
                fieldName
                    ?.trim()
                    .orEmpty()
        }
    }

    private fun getFirst(
        values: Map<String, List<String>>,
        name: String
    ): String? {
        return values[
            name
        ]
            ?.firstOrNull()
            ?.trim()
    }

    private fun getInteger(
        values: Map<String, List<String>>,
        name: String
    ): Int? {
        return getFirst(
            values,
            name
        )
            ?.takeIf {
                it.isNotBlank()
            }
            ?.toIntOrNull()
    }

    private fun getFirstInteger(
        values: Map<String, List<String>>,
        names: List<String>
    ): Int? {
        for (name in names) {
            val value =
                values[
                    name
                ]
                    ?.firstOrNull {
                        it.trim()
                            .toIntOrNull() !=
                                null
                    }
                    ?.trim()
                    ?.toIntOrNull()

            if (value != null) {
                return value
            }
        }

        return null
    }

    private fun getBoolean(
        values: Map<String, List<String>>,
        name: String
    ): Boolean {
        return when (
            getFirst(
                values,
                name
            )
                ?.lowercase()
        ) {
            "true",
            "1",
            "yes",
            "on" ->
                true

            else ->
                false
        }
    }
}
