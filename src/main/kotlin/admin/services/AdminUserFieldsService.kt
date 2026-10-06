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
import com.hr.admin.dtos.AdminUserFieldChoiceResponse
import com.hr.admin.dtos.AdminUserFieldItemResponse
import com.hr.admin.dtos.AdminUserFieldResponse
import com.hr.admin.repositories.AdminUserFieldsRepository
import com.hr.auth.repositories.AuthRepository

object AdminUserFieldsService {

    suspend fun getUserFields(
        accountId: Int
    ): AdminUserFieldsResult {
        println(
            "=================================================="
        )

        println(
            "Admin user-fields service started"
        )

        println(
            "Authenticated account ID: $accountId"
        )

        val account =
            try {
                AuthRepository
                    .findAccountById(
                        accountId
                    )
            } catch (exception: Exception) {
                println(
                    "Unable to retrieve authenticated Admin account"
                )

                printExceptionDetails(
                    exception
                )

                println(
                    "=================================================="
                )

                return AdminUserFieldsResult
                    .Failed
            }
                ?: run {
                    println(
                        "Authenticated Admin account was not found"
                    )

                    println(
                        "Account ID: $accountId"
                    )

                    println(
                        "=================================================="
                    )

                    return AdminUserFieldsResult
                        .AccountNotFound
                }

        println(
            "Authenticated account found"
        )

        println(
            "Account ID: ${account.id}"
        )

        println(
            "User ID: ${account.userId}"
        )

        println(
            "Role: ${account.role}"
        )

        println(
            "Active: ${account.isActive}"
        )

        if (!account.isActive) {
            println(
                "Admin user-fields request rejected"
            )

            println(
                "Reason: Authenticated account is inactive"
            )

            println(
                "=================================================="
            )

            return AdminUserFieldsResult
                .AccountInactive
        }

        /*
  * No role restriction is applied here.
  *
  * Every authenticated active account can retrieve
  * the complete user-field metadata.
  */
        println(
            "Admin user-fields access granted"
        )

        println(
            "Account ID: ${account.id}"
        )

        println(
            "Account role: ${account.role}"
        )

        println(
            "All authenticated active roles are permitted"
        )







        return try {
            println(
                "Retrieving Admin user-field reference values"
            )

            val academicQualifications =
                AdminUserFieldsRepository
                    .getAcademicQualifications()

            println(
                "Academic qualifications retrieved: ${academicQualifications.size}"
            )

            val departments =
                AdminUserFieldsRepository
                    .getDepartments()

            println(
                "Departments retrieved: ${departments.size}"
            )

            val classes =
                AdminUserFieldsRepository
                    .getClasses()

            println(
                "Classes retrieved: ${classes.size}"
            )

            val districts =
                AdminUserFieldsRepository
                    .getDistricts()

            println(
                "Districts retrieved: ${districts.size}"
            )

            val regions =
                AdminUserFieldsRepository
                    .getRegions()

            println(
                "Regions retrieved: ${regions.size}"
            )

            val currentGrades =
                AdminUserFieldsRepository
                    .getCurrentGrades()

            println(
                "Current grades retrieved: ${currentGrades.size}"
            )

            val nextGrades =
                AdminUserFieldsRepository
                    .getNextGrades()

            println(
                "Next grades retrieved: ${nextGrades.size}"
            )

            val changeOfGrades =
                AdminUserFieldsRepository
                    .getChangeOfGrades()

            println(
                "Change-of-grade values retrieved: ${changeOfGrades.size}"
            )

            val managementUnits =
                AdminUserFieldsRepository
                    .getManagementUnits()

            println(
                "Management units retrieved: ${managementUnits.size}"
            )

            val titles =
                AdminUserFieldsRepository
                    .getTitles()

            println(
                "Titles retrieved: ${titles.size}"
            )

            val leaveTypes =
                AdminUserFieldsRepository
                    .getLeaveTypes()

            println(
                "Leave types retrieved: ${leaveTypes.size}"
            )

            val fields =
                listOf(
                    textField(
                        name =
                            "userId",

                        required =
                            true
                    ),

                    choiceField(
                        name =
                            "role",

                        values =
                            Role.entries.map {
                                it.name
                            },

                        required =
                            true
                    ),

                    textField(
                        name =
                            "firstName",

                        required =
                            true
                    ),

                    textField(
                        name =
                            "middleName"
                    ),

                    textField(
                        name =
                            "lastName",

                        required =
                            true
                    ),

                    textField(
                        name =
                            "maidenName"
                    ),

                    choiceField(
                        name =
                            "gender",

                        values =
                            Gender.entries.map {
                                it.name
                            }
                    ),

                    dateField(
                        name =
                            "dateOfBirth"
                    ),

                    choiceField(
                        name =
                            "maritalStatus",

                        values =
                            MaritalStatus.entries.map {
                                it.name
                            }
                    ),

                    integerField(
                        name =
                            "standardRetirementAge"
                    ),

                    foreignKeyField(
                        name =
                            "academicQualificationId",

                        items =
                            academicQualifications
                    ),

                    foreignKeyField(
                        name =
                            "directorateId",

                        items =
                            departments
                    ),

                    foreignKeyField(
                        name =
                            "categoryId",

                        items =
                            classes
                    ),

                    foreignKeyField(
                        name =
                            "districtId",

                        items =
                            districts
                    ),

                    foreignKeyField(
                        name =
                            "regionId",

                        items =
                            regions
                    ),

                    foreignKeyField(
                        name =
                            "currentGradeId",

                        items =
                            currentGrades
                    ),

                    foreignKeyField(
                        name =
                            "nextGradeId",

                        items =
                            nextGrades
                    ),

                    foreignKeyField(
                        name =
                            "changeOfGradeId",

                        items =
                            changeOfGrades
                    ),

                    foreignKeyField(
                        name =
                            "managementUnitCostCentreId",

                        items =
                            managementUnits
                    ),

                    foreignKeyField(
                        name =
                            "titleId",

                        items =
                            titles
                    ),

                    foreignKeyField(
                        name =
                            "onLeaveTypeId",

                        items =
                            leaveTypes
                    ),

                    choiceField(
                        name =
                            "professional",

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
                        name =
                            "staffCategory",

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
                        name =
                            "currentSalaryLevel",

                        values =
                            SalaryLevel.entries.map {
                                it.name
                            }
                    ),

                    choiceField(
                        name =
                            "currentSalaryPoint",

                        values =
                            SalaryPoint.entries.map {
                                it.name
                            }
                    ),

                    choiceField(
                        name =
                            "nextSalaryLevel",

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
                        name =
                            "substantiveDate"
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
                        name =
                            "monthlyGrossPay"
                    ),

                    decimalField(
                        name =
                            "annualSalary"
                    ),

                    integerField(
                        name =
                            "numberOfFocusAreas"
                    ),

                    integerField(
                        name =
                            "numberOfTargets"
                    ),

                    integerField(
                        name =
                            "numberOfTargetsMet"
                    ),

                    integerField(
                        name =
                            "numberOfTargetsNotMet"
                    ),

                    decimalField(
                        name =
                            "overallAssessmentScore"
                    ),

                    textAreaField(
                        name =
                            "selfAssessmentDescription"
                    ),

                    textField(
                        name =
                            "phoneNumber"
                    ),

                    textField(
                        name =
                            "email",

                        type =
                            "EmailField"
                    ),

                    textField(
                        name =
                            "ghanaCardNumber"
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
                        name =
                            "bankName"
                    ),

                    textField(
                        name =
                            "bankAccountNumber"
                    ),

                    textField(
                        name =
                            "bankAccountBranch"
                    ),

                    choiceField(
                        name =
                            "payrollStatus",

                        values =
                            PayrollStatus.entries.map {
                                it.name
                            }
                    ),

                    choiceField(
                        name =
                            "atPostOnLeave",

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
                        name =
                            "supervisorName"
                    ),

                    textField(
                        name =
                            "profilePictureUrl",

                        type =
                            "URLField"
                    )
                )

            println(
                "Admin user-field metadata retrieved successfully"
            )

            println(
                "Authenticated Admin account ID: ${account.id}"
            )

            println(
                "Field count: ${fields.size}"
            )

            fields.forEachIndexed {
                    index,
                    field ->

                println(
                    "Field ${index + 1}: " +
                            "name=${field.fieldName}, " +
                            "type=${field.fieldType}, " +
                            "required=${field.required}, " +
                            "multiple=${field.multiple}, " +
                            "choiceCount=${field.choices?.size ?: 0}, " +
                            "itemCount=${field.items?.size ?: 0}"
                )
            }

            println(
                "=================================================="
            )

            AdminUserFieldsResult.Success(
                fields =
                    fields
            )
        } catch (exception: Exception) {
            println(
                "Unable to retrieve Admin user fields"
            )

            println(
                "Authenticated account ID: ${account.id}"
            )

            printExceptionDetails(
                exception
            )

            println(
                "=================================================="
            )

            AdminUserFieldsResult
                .Failed
        }
    }

    private fun textField(
        name: String,
        type: String =
            "CharField",
        required: Boolean =
            false
    ): AdminUserFieldResponse {
        return AdminUserFieldResponse(
            fieldName =
                name,

            fieldType =
                type,

            multiple =
                false,

            required =
                required,

            choices =
                null,

            items =
                null
        )
    }

    private fun textAreaField(
        name: String,
        required: Boolean =
            false
    ): AdminUserFieldResponse {
        return AdminUserFieldResponse(
            fieldName =
                name,

            fieldType =
                "TextField",

            multiple =
                false,

            required =
                required,

            choices =
                null,

            items =
                null
        )
    }

    private fun dateField(
        name: String,
        required: Boolean =
            false
    ): AdminUserFieldResponse {
        return AdminUserFieldResponse(
            fieldName =
                name,

            fieldType =
                "DateField",

            multiple =
                false,

            required =
                required,

            choices =
                null,

            items =
                null
        )
    }

    private fun integerField(
        name: String,
        required: Boolean =
            false
    ): AdminUserFieldResponse {
        return AdminUserFieldResponse(
            fieldName =
                name,

            fieldType =
                "IntegerField",

            multiple =
                false,

            required =
                required,

            choices =
                null,

            items =
                null
        )
    }

    private fun decimalField(
        name: String,
        required: Boolean =
            false
    ): AdminUserFieldResponse {
        return AdminUserFieldResponse(
            fieldName =
                name,

            fieldType =
                "DecimalField",

            multiple =
                false,

            required =
                required,

            choices =
                null,

            items =
                null
        )
    }

    private fun choiceField(
        name: String,
        values: List<String>,
        required: Boolean =
            false
    ): AdminUserFieldResponse {
        val choices =
            values
                .distinct()
                .map { value ->
                    AdminUserFieldChoiceResponse(
                        value =
                            value,

                        label =
                            formatChoiceLabel(
                                value
                            )
                    )
                }

        return AdminUserFieldResponse(
            fieldName =
                name,

            fieldType =
                "ChoiceField",

            multiple =
                false,

            required =
                required,

            choices =
                choices,

            items =
                null
        )
    }

    private fun foreignKeyField(
        name: String,
        items:
        List<AdminUserFieldItemResponse>,
        required: Boolean =
            false
    ): AdminUserFieldResponse {
        return AdminUserFieldResponse(
            fieldName =
                name,

            fieldType =
                "ForeignKey",

            multiple =
                false,

            required =
                required,

            choices =
                null,

            items =
                items
                    .distinctBy {
                        it.id
                    }
                    .sortedBy {
                        it.name.lowercase()
                    }
        )
    }

    private fun formatChoiceLabel(
        value: String
    ): String {
        return value
            .replace(
                oldChar =
                    '_',

                newChar =
                    ' '
            )
            .lowercase()
            .split(
                " "
            )
            .filter {
                it.isNotBlank()
            }
            .joinToString(
                separator =
                    " "
            ) { word ->
                word.replaceFirstChar {
                        character ->

                    character.uppercase()
                }
            }
    }

    private fun printExceptionDetails(
        exception: Exception
    ) {
        println(
            "Error type: ${exception::class.simpleName}"
        )

        println(
            "Error message: ${exception.message}"
        )

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
}