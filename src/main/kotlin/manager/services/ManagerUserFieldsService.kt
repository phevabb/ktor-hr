package com.hr.manager.services

import com.hr.account.dtos.AccommodationStatus
import com.hr.account.dtos.AtPostOnLeave
import com.hr.account.dtos.FulltimeContractStaff
import com.hr.account.dtos.Gender
import com.hr.account.dtos.MaritalStatus
import com.hr.account.dtos.PayrollStatus
import com.hr.account.dtos.Professional
import com.hr.account.dtos.SalaryLevel
import com.hr.account.dtos.SalaryPoint
import com.hr.account.dtos.StaffCategory
import com.hr.manager.dtos.ManagerUserFieldChoiceResponse
import com.hr.manager.dtos.ManagerUserFieldResponse
import com.hr.manager.repositories.ManagerUserFieldsRepository
import com.hr.manager.repositories.ManagerUsersRepository

object ManagerUserFieldsService {

    suspend fun getUserFields(
        managerAccountId: Int
    ): ManagerUserFieldsResult {
        println(
            "=================================================="
        )

        println(
            "Manager user-fields service started"
        )

        println(
            "Authenticated account ID: $managerAccountId"
        )

        val managerRow =
            try {
                ManagerUsersRepository
                    .findManagerAccount(
                        accountId =
                            managerAccountId
                    )
            } catch (exception: Exception) {
                printFailure(
                    message =
                        "Unable to retrieve authenticated Manager account",

                    exception =
                        exception
                )

                return ManagerUserFieldsResult
                    .Failed
            }
                ?: run {
                    println(
                        "Authenticated Manager account was not found"
                    )

                    println(
                        "Account ID: $managerAccountId"
                    )

                    println(
                        "=================================================="
                    )

                    return ManagerUserFieldsResult
                        .AccountNotFound
                }

        val managerIsActive =
            ManagerUsersRepository
                .isActive(
                    managerRow
                )

        println(
            "Manager account active: $managerIsActive"
        )

        if (!managerIsActive) {
            println(
                "Manager user-fields request rejected"
            )

            println(
                "Reason: Manager account is inactive"
            )

            println(
                "=================================================="
            )

            return ManagerUserFieldsResult
                .AccountInactive
        }

        val accountIsManager =
            ManagerUsersRepository
                .isManager(
                    managerRow
                )

        println(
            "Account has Manager role: $accountIsManager"
        )

        if (!accountIsManager) {
            println(
                "Manager user-fields request rejected"
            )

            println(
                "Reason: Authenticated account is not a Manager"
            )

            println(
                "=================================================="
            )

            return ManagerUserFieldsResult
                .AccessDenied
        }

        val managerRegionId =
            ManagerUsersRepository
                .getManagerRegionId(
                    managerRow
                )
                ?: run {
                    println(
                        "Manager user-fields request rejected"
                    )

                    println(
                        "Reason: Manager does not have an assigned region"
                    )

                    println(
                        "=================================================="
                    )

                    return ManagerUserFieldsResult
                        .RegionNotAssigned
                }

        println(
            "Manager region ID: $managerRegionId"
        )

        return try {
            val academicQualifications =
                ManagerUserFieldsRepository
                    .getAcademicQualifications()

            val directorates =
                ManagerUserFieldsRepository
                    .getDirectorates()

            val staffClasses =
                ManagerUserFieldsRepository
                    .getStaffClasses()

            val districts =
                ManagerUserFieldsRepository
                    .getDistricts()

            val currentGrades =
                ManagerUserFieldsRepository
                    .getCurrentGrades()

            val nextGrades =
                ManagerUserFieldsRepository
                    .getNextGrades()

            val changeOfGrades =
                ManagerUserFieldsRepository
                    .getChangeOfGrades()

            val managementUnits =
                ManagerUserFieldsRepository
                    .getManagementUnits()

            val titles =
                ManagerUserFieldsRepository
                    .getTitles()

            val leaveTypes =
                ManagerUserFieldsRepository
                    .getLeaveTypes()

            val fields =
                listOf(
                    textField(
                        name =
                            "userId",

                        type =
                            "CharField",

                        required =
                            true
                    ),

                    textField(
                        name =
                            "firstName",

                        type =
                            "CharField",

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

                        type =
                            "CharField",

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
                            directorates
                    ),

                    foreignKeyField(
                        name =
                            "categoryId",

                        items =
                            staffClasses
                    ),

                    foreignKeyField(
                        name =
                            "districtId",

                        items =
                            districts
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
                            FulltimeContractStaff.entries.map {
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
                            AccommodationStatus.entries.map {
                                it.name
                            }
                    ),

                    textField(
                        name =
                            "supervisorName"
                    ),

                    textField(
                        name =
                            "email",

                        type =
                            "EmailField"
                    ),

                    textField(
                        name =
                            "profilePictureUrl",

                        type =
                            "URLField"
                    )
                )

            println(
                "Manager user fields created successfully"
            )

            println(
                "Field count: ${fields.size}"
            )

            fields.forEach { field ->
                println(
                    "Field: ${field.fieldName}, " +
                            "type=${field.fieldType}, " +
                            "required=${field.required}"
                )
            }

            println(
                "=================================================="
            )

            ManagerUserFieldsResult.Success(
                fields =
                    fields
            )
        } catch (exception: Exception) {
            printFailure(
                message =
                    "Manager user-fields service failed",

                exception =
                    exception
            )

            ManagerUserFieldsResult.Failed
        }
    }

    private fun textField(
        name: String,
        type: String =
            "CharField",
        required: Boolean =
            false
    ): ManagerUserFieldResponse {
        return ManagerUserFieldResponse(
            fieldName =
                name,

            fieldType =
                type,

            required =
                required
        )
    }

    private fun textAreaField(
        name: String,
        required: Boolean =
            false
    ): ManagerUserFieldResponse {
        return ManagerUserFieldResponse(
            fieldName =
                name,

            fieldType =
                "TextField",

            required =
                required
        )
    }

    private fun dateField(
        name: String,
        required: Boolean =
            false
    ): ManagerUserFieldResponse {
        return ManagerUserFieldResponse(
            fieldName =
                name,

            fieldType =
                "DateField",

            required =
                required
        )
    }

    private fun integerField(
        name: String,
        required: Boolean =
            false
    ): ManagerUserFieldResponse {
        return ManagerUserFieldResponse(
            fieldName =
                name,

            fieldType =
                "IntegerField",

            required =
                required
        )
    }

    private fun decimalField(
        name: String,
        required: Boolean =
            false
    ): ManagerUserFieldResponse {
        return ManagerUserFieldResponse(
            fieldName =
                name,

            fieldType =
                "DecimalField",

            required =
                required
        )
    }

    private fun choiceField(
        name: String,
        values: List<String>,
        required: Boolean =
            false
    ): ManagerUserFieldResponse {
        return ManagerUserFieldResponse(
            fieldName =
                name,

            fieldType =
                "ChoiceField",

            required =
                required,

            choices =
                values.map { value ->
                    ManagerUserFieldChoiceResponse(
                        value =
                            value,

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
        List<com.hr.manager.dtos.ManagerUserFieldItemResponse>,
        required: Boolean =
            false
    ): ManagerUserFieldResponse {
        return ManagerUserFieldResponse(
            fieldName =
                name,

            fieldType =
                "ForeignKey",

            required =
                required,

            items =
                items
        )
    }

    private fun formatChoiceLabel(
        value: String
    ): String {
        return value
            .replace(
                oldChar = '_',
                newChar = ' '
            )
    }

    private fun printFailure(
        message: String,
        exception: Exception
    ) {
        println(
            message
        )

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

        println(
            "=================================================="
        )
    }
}