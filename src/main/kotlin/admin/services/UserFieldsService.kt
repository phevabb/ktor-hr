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
        println(
            "=================================================="
        )

        println(
            "Manager user fields service started"
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
                    "Unable to retrieve authenticated account"
                )

                println(
                    "Account ID: $accountId"
                )

                println(
                    "Error type: ${exception::class.simpleName}"
                )

                println(
                    "Error message: ${exception.message}"
                )

                exception.cause?.let { cause ->
                    println(
                        "Cause type: ${cause::class.simpleName}"
                    )

                    println(
                        "Cause message: ${cause.message}"
                    )
                }

                println(
                    "=================================================="
                )

                return UserFieldsResult
                    .Failed
            }
                ?: run {
                    println(
                        "Authenticated Manager account was not found"
                    )

                    println(
                        "Account ID: $accountId"
                    )

                    println(
                        "=================================================="
                    )

                    return UserFieldsResult
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
            "Account active: ${account.isActive}"
        )

        if (!account.isActive) {
            println(
                "Manager user fields access denied"
            )

            println(
                "Reason: Account is inactive"
            )

            println(
                "Account ID: ${account.id}"
            )

            println(
                "=================================================="
            )

            return UserFieldsResult
                .AccountInactive
        }

        val isManager =
            account.role.equals(
                other = "Manager",
                ignoreCase = true
            )

        println(
            "Account has Manager role: $isManager"
        )

        if (!isManager) {
            println(
                "Manager user fields access denied"
            )

            println(
                "Account ID: ${account.id}"
            )

            println(
                "Current role: ${account.role}"
            )

            println(
                "Required role: Manager"
            )

            println(
                "=================================================="
            )

            return UserFieldsResult
                .AccessDenied
        }

        println(
            "Manager user fields access granted"
        )

        println(
            "Account ID: ${account.id}"
        )

        println(
            "Role: ${account.role}"
        )

        return try {
            println(
                "Retrieving academic qualifications"
            )

            val academicQualifications =
                UserFieldsRepository
                    .getAcademicQualifications()

            println(
                "Academic qualifications retrieved: ${academicQualifications.size}"
            )

            println(
                "Retrieving departments"
            )

            val departments =
                UserFieldsRepository
                    .getDepartments()

            println(
                "Departments retrieved: ${departments.size}"
            )

            println(
                "Retrieving staff classes"
            )

            val classes =
                UserFieldsRepository
                    .getClasses()

            println(
                "Staff classes retrieved: ${classes.size}"
            )

            println(
                "Retrieving districts"
            )

            val districts =
                UserFieldsRepository
                    .getDistricts()

            println(
                "Districts retrieved: ${districts.size}"
            )

            /*
             * Regions are intentionally not retrieved.
             *
             * ManagerCreateUserService assigns the
             * authenticated Manager's region automatically.
             */

            println(
                "Retrieving current grades"
            )

            val currentGrades =
                UserFieldsRepository
                    .getCurrentGrades()

            println(
                "Current grades retrieved: ${currentGrades.size}"
            )

            println(
                "Retrieving next grades"
            )

            val nextGrades =
                UserFieldsRepository
                    .getNextGrades()

            println(
                "Next grades retrieved: ${nextGrades.size}"
            )

            println(
                "Retrieving change-of-grade options"
            )

            val changeOfGrades =
                UserFieldsRepository
                    .getChangeOfGrades()

            println(
                "Change-of-grade options retrieved: ${changeOfGrades.size}"
            )

            println(
                "Retrieving management units"
            )

            val managementUnits =
                UserFieldsRepository
                    .getManagementUnits()

            println(
                "Management units retrieved: ${managementUnits.size}"
            )

            println(
                "Retrieving titles"
            )

            val titles =
                UserFieldsRepository
                    .getTitles()

            println(
                "Titles retrieved: ${titles.size}"
            )

            println(
                "Retrieving leave types"
            )

            val leaveTypes =
                UserFieldsRepository
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

                    /*
                     * Role is intentionally excluded.
                     *
                     * ManagerCreateUserService forces:
                     *
                     * role = Role.Staff
                     */

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

                    textField(
                        name =
                            "email",

                        type =
                            "EmailField"
                    ),

                    textField(
                        name =
                            "phoneNumber"
                    ),

                    choiceField(
                        name =
                            "gender",

                        values =
                            Gender.entries.map {
                                it.name
                            }
                    ),

                    choiceField(
                        name =
                            "maritalStatus",

                        values =
                            MaritalStatus.entries.map {
                                it.name
                            }
                    ),

                    dateField(
                        name =
                            "dateOfBirth"
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

                    /*
                     * regionId is intentionally excluded.
                     *
                     * ManagerCreateUserService assigns:
                     *
                     * regionId = managerRegionId
                     */

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

                    textField(
                        name =
                            "selfAssessmentDescription",

                        type =
                            "TextField"
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
                            "bankAccountBranch"
                    ),

                    textField(
                        name =
                            "bankAccountNumber"
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
                    )
                )

            println(
                "Manager user field metadata retrieved successfully"
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
                            "multiple=${field.multiple}"
                )
            }

            println(
                "Manager-controlled fields excluded:"
            )

            println(
                "role"
            )

            println(
                "regionId"
            )

            println(
                "isActive"
            )

            println(
                "isStaff"
            )

            println(
                "isSuperuser"
            )

            println(
                "password"
            )

            println(
                "=================================================="
            )

            UserFieldsResult.Success(
                fields =
                    fields
            )
        } catch (exception: Exception) {
            println(
                "Unable to retrieve Manager user fields"
            )

            println(
                "Account ID: ${account.id}"
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
