package com.hr.admin.services

import com.hr.account.dtos.AccommodationStatus
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
import com.hr.admin.dtos.UserFieldChoiceResponse
import com.hr.admin.dtos.UserFieldMetadataResponse
import com.hr.admin.repositories.UserFieldsRepository
import com.hr.auth.repositories.AuthRepository

object UserFieldsService {

    suspend fun getUserFields(
        accountId: Int
    ): UserFieldsResult {
        val account =
            AuthRepository
                .findAccountById(
                    accountId
                )
                ?: return UserFieldsResult
                    .AccountNotFound

        if (!account.isActive) {
            return UserFieldsResult
                .AccountInactive
        }

        val isAdmin =
            account.role.equals(
                other = "Admin",
                ignoreCase = true
            )

        if (!isAdmin) {
            println(
                "User fields access denied: " +
                        "accountId=${account.id}, " +
                        "role=${account.role}"
            )

            return UserFieldsResult
                .AccessDenied
        }

        return try {
            val academicQualifications =
                UserFieldsRepository
                    .getAcademicQualifications()

            val departments =
                UserFieldsRepository
                    .getDepartments()

            val classes =
                UserFieldsRepository
                    .getClasses()

            val districts =
                UserFieldsRepository
                    .getDistricts()

            val regions =
                UserFieldsRepository
                    .getRegions()

            val currentGrades =
                UserFieldsRepository
                    .getCurrentGrades()

            val nextGrades =
                UserFieldsRepository
                    .getNextGrades()

            val changeOfGrades =
                UserFieldsRepository
                    .getChangeOfGrades()

            val managementUnits =
                UserFieldsRepository
                    .getManagementUnits()

            val titles =
                UserFieldsRepository
                    .getTitles()

            val leaveTypes =
                UserFieldsRepository
                    .getLeaveTypes()

            val fields =
                listOf(
                    textField(
                        name = "userId",
                        required = true
                    ),



                    choiceField(
                        name = "role",
                        values =
                            Role.entries.map {
                                it.name
                            },
                        required = true
                    ),

                    textField(
                        name = "firstName"
                    ),

                    textField(
                        name = "middleName"
                    ),

                    textField(
                        name = "lastName"
                    ),

                    textField(
                        name = "maidenName"
                    ),

                    textField(
                        name = "email",
                        type = "EmailField"
                    ),

                    textField(
                        name = "phoneNumber"
                    ),

                    choiceField(
                        name = "gender",
                        values =
                            Gender.entries.map {
                                it.name
                            }
                    ),

                    choiceField(
                        name = "maritalStatus",
                        values =
                            MaritalStatus.entries.map {
                                it.name
                            }
                    ),

                    dateField(
                        name = "dateOfBirth"
                    ),

                    foreignKeyField(
                        name =
                            "academicQualificationId",
                        items =
                            academicQualifications
                    ),

                    foreignKeyField(
                        name = "directorateId",
                        items = departments
                    ),

                    foreignKeyField(
                        name = "categoryId",
                        items = classes
                    ),

                    foreignKeyField(
                        name = "districtId",
                        items = districts
                    ),

                    foreignKeyField(
                        name = "regionId",
                        items = regions
                    ),

                    foreignKeyField(
                        name = "currentGradeId",
                        items = currentGrades
                    ),

                    foreignKeyField(
                        name = "nextGradeId",
                        items = nextGrades
                    ),

                    foreignKeyField(
                        name = "changeOfGradeId",
                        items = changeOfGrades
                    ),

                    foreignKeyField(
                        name =
                            "managementUnitCostCentreId",
                        items =
                            managementUnits
                    ),

                    foreignKeyField(
                        name = "titleId",
                        items = titles
                    ),

                    foreignKeyField(
                        name = "onLeaveTypeId",
                        items = leaveTypes
                    ),

                    choiceField(
                        name = "professional",
                        values =
                            Professional.entries.map {
                                it.name
                            }
                    ),

                    textField(
                        name =
                            "professionalQualification"
                    ),

                    choiceField(
                        name = "staffCategory",
                        values =
                            StaffCategory.entries.map {
                                it.name
                            }
                    ),

                    choiceField(
                        name =
                            "fulltimeContractStaff",
                        values =
                            FulltimeContractStaff
                                .entries
                                .map {
                                    it.name
                                }
                    ),

                    choiceField(
                        name = "currentSalaryLevel",
                        values =
                            SalaryLevel.entries.map {
                                it.name
                            }
                    ),

                    choiceField(
                        name = "currentSalaryPoint",
                        values =
                            SalaryPoint.entries.map {
                                it.name
                            }
                    ),

                    choiceField(
                        name = "nextSalaryLevel",
                        values =
                            SalaryLevel.entries.map {
                                it.name
                            }
                    ),

                    dateField(
                        name =
                            "dateOfAssumptionOfDuty"
                    ),

                    dateField(
                        name = "substantiveDate"
                    ),

                    dateField(
                        name =
                            "nationalEffectiveDate"
                    ),

                    dateField(
                        name =
                            "dateOfLastPromotion"
                    ),

                    dateField(
                        name =
                            "dateOfFirstAppointment"
                    ),

                    decimalField(
                        name =
                            "singleSpineMonthlySalary"
                    ),

                    decimalField(
                        name = "monthlyGrossPay"
                    ),

                    decimalField(
                        name = "annualSalary"
                    ),

                    integerField(
                        name = "numberOfFocusAreas"
                    ),

                    integerField(
                        name = "numberOfTargets"
                    ),

                    integerField(
                        name = "numberOfTargetsMet"
                    ),

                    integerField(
                        name =
                            "numberOfTargetsNotMet"
                    ),

                    decimalField(
                        name =
                            "overallAssessmentScore"
                    ),

                    textField(
                        name =
                            "selfAssessmentDescription",
                        type = "TextField"
                    ),

                    textField(
                        name = "ghanaCardNumber"
                    ),

                    textField(
                        name =
                            "socialSecurityNumber"
                    ),

                    textField(
                        name =
                            "nationalHealthInsuranceNumber"
                    ),

                    textField(
                        name = "bankName"
                    ),

                    textField(
                        name = "bankAccountBranch"
                    ),

                    textField(
                        name = "bankAccountNumber"
                    ),

                    choiceField(
                        name = "payrollStatus",
                        values =
                            PayrollStatus.entries.map {
                                it.name
                            }
                    ),

                    choiceField(
                        name = "atPostOnLeave",
                        values =
                            AtPostOnLeave.entries.map {
                                it.name
                            }
                    ),

                    choiceField(
                        name =
                            "accommodationStatus",
                        values =
                            AccommodationStatus
                                .entries
                                .map {
                                    it.name
                                }
                    ),

                    textField(
                        name = "supervisorName"
                    )
                )

            println(
                "User field metadata retrieved: " +
                        "accountId=${account.id}, " +
                        "fieldCount=${fields.size}"
            )

            UserFieldsResult.Success(
                fields = fields
            )
        } catch (exception: Exception) {
            println(
                "Unable to retrieve user fields: " +
                        "accountId=${account.id}, " +
                        "errorType=${exception::class.simpleName}, " +
                        "message=${exception.message}"
            )

            UserFieldsResult.Failed
        }
    }

    private fun textField(
        name: String,
        type: String = "CharField",
        required: Boolean = false
    ): UserFieldMetadataResponse {
        return UserFieldMetadataResponse(
            fieldName = name,
            fieldType = type,
            required = required
        )
    }


    private fun dateField(
        name: String
    ): UserFieldMetadataResponse {
        return UserFieldMetadataResponse(
            fieldName = name,
            fieldType = "DateField"
        )
    }

    private fun integerField(
        name: String
    ): UserFieldMetadataResponse {
        return UserFieldMetadataResponse(
            fieldName = name,
            fieldType = "IntegerField"
        )
    }

    private fun decimalField(
        name: String
    ): UserFieldMetadataResponse {
        return UserFieldMetadataResponse(
            fieldName = name,
            fieldType = "DecimalField"
        )
    }

    private fun choiceField(
        name: String,
        values: List<String>,
        required: Boolean = false
    ): UserFieldMetadataResponse {
        return UserFieldMetadataResponse(
            fieldName = name,
            fieldType = "ChoiceField",
            required = required,
            choices =
                values.map { value ->
                    UserFieldChoiceResponse(
                        value = value,
                        label =
                            formatChoiceLabel(
                                value
                            )
                    )
                }
        )
    }

    private fun foreignKeyField(
        name: String,
        items:
        List<com.hr.admin.dtos.UserFieldItemResponse>
    ): UserFieldMetadataResponse {
        return UserFieldMetadataResponse(
            fieldName = name,
            fieldType = "ForeignKey",
            items = items
        )
    }

    private fun formatChoiceLabel(
        value: String
    ): String {
        return value
            .lowercase()
            .split("_")
            .joinToString(" ") { word ->
                word.replaceFirstChar {
                        character ->

                    character.uppercase()
                }
            }
    }
}
