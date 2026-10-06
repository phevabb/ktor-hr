package com.hr.admin.repositories

import com.hr.academicqualification.table.AcademicQualifications
import com.hr.account.dtos.AccommodationStatus
import com.hr.account.dtos.AccountResponse
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
import com.hr.account.repositories.AccountRepository
import com.hr.account.table.Accounts
import com.hr.admin.dtos.AdminUserUpdateRequest
import com.hr.changeofgrade.tables.ChangeOfGrades
import com.hr.currentgrade.tables.CurrentGrades
import com.hr.department.table.Departments
import com.hr.districts.tables.Districts
import com.hr.managementUnits.tables.ManagementUnits
import com.hr.nextgrade.tables.NextGrades
import com.hr.onleavetype.table.OnLeaveTypes
import com.hr.region.tables.Regions
import com.hr.staffclass.table.StaffClasses
import com.hr.title.table.Titles
import java.time.LocalDate
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object AdminUserUpdateRepository {

    suspend fun findAccountRow(
        accountId: Int
    ): ResultRow? {
        println(
            "Searching for account to update"
        )

        println(
            "Account ID: $accountId"
        )

        return Accounts
            .selectAll()
            .where {
                Accounts.id eq
                        accountId
            }
            .firstOrNull()
    }

    suspend fun userIdBelongsToAnotherAccount(
        accountId: Int,
        userId: String
    ): Boolean {
        val normalizedUserId =
            userId.trim()

        if (normalizedUserId.isBlank()) {
            return false
        }

        val row =
            Accounts
                .selectAll()
                .where {
                    Accounts.userId eq
                            normalizedUserId
                }
                .firstOrNull()

        return (
                row != null &&
                        row[
                            Accounts.id
                        ].value !=
                        accountId
                )
    }

    suspend fun updateAccount(
        accountId: Int,
        request: AdminUserUpdateRequest
    ): Boolean {
        println(
            "=================================================="
        )

        println(
            "Admin account update repository started"
        )

        println(
            "Target account ID: $accountId"
        )

        println(
            "Provided fields: ${request.providedFields}"
        )

        val updatedRows =
            Accounts.update(
                where = {
                    Accounts.id eq
                            accountId
                }
            ) { statement ->

                /*
                 * Basic account fields
                 */

                if (
                    request.has(
                        "userId"
                    )
                ) {
                    statement[
                        Accounts.userId
                    ] =
                        request.userId
                            ?.trim()
                }

                if (
                    request.has(
                        "email"
                    )
                ) {
                    statement[
                        Accounts.email
                    ] =
                        request.email
                            .normalizeNullableText()
                }

                if (
                    request.has(
                        "firstName"
                    )
                ) {
                    statement[
                        Accounts.firstName
                    ] =
                        request.firstName
                            .normalizeNullableText()
                }

                if (
                    request.has(
                        "middleName"
                    )
                ) {
                    statement[
                        Accounts.middleName
                    ] =
                        request.middleName
                            .normalizeNullableText()
                }

                if (
                    request.has(
                        "lastName"
                    )
                ) {
                    statement[
                        Accounts.lastName
                    ] =
                        request.lastName
                            .normalizeNullableText()
                }

                if (
                    request.has(
                        "maidenName"
                    )
                ) {
                    statement[
                        Accounts.maidenName
                    ] =
                        request.maidenName
                            .normalizeNullableText()
                }

                /*
                 * Enum fields
                 */

                if (
                    request.has(
                        "role"
                    )
                ) {
                    request.role
                        ?.let {
                            parseEnumValue<Role>(
                                it
                            )
                        }
                        ?.let { role ->
                            statement[
                                Accounts.role
                            ] =
                                role
                        }
                }

                if (
                    request.has(
                        "gender"
                    )
                ) {
                    request.gender
                        ?.let {
                            parseEnumValue<Gender>(
                                it
                            )
                        }
                        ?.let { gender ->
                            statement[
                                Accounts.gender
                            ] =
                                gender
                        }
                }

                if (
                    request.has(
                        "maritalStatus"
                    )
                ) {
                    request.maritalStatus
                        ?.let {
                            parseEnumValue<MaritalStatus>(
                                it
                            )
                        }
                        ?.let { maritalStatus ->
                            statement[
                                Accounts.maritalStatus
                            ] =
                                maritalStatus
                        }
                }

                if (
                    request.has(
                        "professional"
                    )
                ) {
                    request.professional
                        ?.let {
                            parseEnumValue<Professional>(
                                it
                            )
                        }
                        ?.let { professional ->
                            statement[
                                Accounts.professional
                            ] =
                                professional
                        }
                }

                if (
                    request.has(
                        "staffCategory"
                    )
                ) {
                    request.staffCategory
                        ?.let {
                            parseEnumValue<StaffCategory>(
                                it
                            )
                        }
                        ?.let { staffCategory ->
                            statement[
                                Accounts.staffCategory
                            ] =
                                staffCategory
                        }
                }

                if (
                    request.has(
                        "fulltimeContractStaff"
                    )
                ) {
                    request.fulltimeContractStaff
                        ?.let {
                            parseEnumValue<FulltimeContractStaff>(
                                it
                            )
                        }
                        ?.let { contractType ->
                            statement[
                                Accounts.fulltimeContractStaff
                            ] =
                                contractType
                        }
                }

                if (
                    request.has(
                        "atPostOnLeave"
                    )
                ) {
                    request.atPostOnLeave
                        ?.let {
                            parseEnumValue<AtPostOnLeave>(
                                it
                            )
                        }
                        ?.let { atPostOnLeave ->
                            statement[
                                Accounts.atPostOnLeave
                            ] =
                                atPostOnLeave
                        }
                }

                if (
                    request.has(
                        "payrollStatus"
                    )
                ) {
                    request.payrollStatus
                        ?.let {
                            parseEnumValue<PayrollStatus>(
                                it
                            )
                        }
                        ?.let { payrollStatus ->
                            statement[
                                Accounts.payrollStatus
                            ] =
                                payrollStatus
                        }
                }

                if (
                    request.has(
                        "accommodationStatus"
                    )
                ) {
                    request.accommodationStatus
                        ?.let {
                            parseEnumValue<AccommodationStatus>(
                                it
                            )
                        }
                        ?.let { accommodationStatus ->
                            statement[
                                Accounts.accommodationStatus
                            ] =
                                accommodationStatus
                        }
                }

                if (
                    request.has(
                        "currentSalaryLevel"
                    )
                ) {
                    request.currentSalaryLevel
                        ?.let {
                            parseEnumValue<SalaryLevel>(
                                it
                            )
                        }
                        ?.let { salaryLevel ->
                            statement[
                                Accounts.currentSalaryLevel
                            ] =
                                salaryLevel
                        }
                }

                if (
                    request.has(
                        "currentSalaryPoint"
                    )
                ) {
                    request.currentSalaryPoint
                        ?.let {
                            parseEnumValue<SalaryPoint>(
                                it
                            )
                        }
                        ?.let { salaryPoint ->
                            statement[
                                Accounts.currentSalaryPoint
                            ] =
                                salaryPoint
                        }
                }

                if (
                    request.has(
                        "nextSalaryLevel"
                    )
                ) {
                    request.nextSalaryLevel
                        ?.let {
                            parseEnumValue<SalaryLevel>(
                                it
                            )
                        }
                        ?.let { salaryLevel ->
                            statement[
                                Accounts.nextSalaryLevel
                            ] =
                                salaryLevel
                        }
                }

                /*
                 * Text fields
                 */

                if (
                    request.has(
                        "professionalQualification"
                    )
                ) {
                    statement[
                        Accounts.professionalQualification
                    ] =
                        request.professionalQualification
                            .normalizeNullableText()
                }

                if (
                    request.has(
                        "phoneNumber"
                    )
                ) {
                    statement[
                        Accounts.phoneNumber
                    ] =
                        request.phoneNumber
                            .normalizeNullableText()
                }

                if (
                    request.has(
                        "ghanaCardNumber"
                    )
                ) {
                    statement[
                        Accounts.ghanaCardNumber
                    ] =
                        request.ghanaCardNumber
                            .normalizeNullableText()
                }

                if (
                    request.has(
                        "socialSecurityNumber"
                    )
                ) {
                    statement[
                        Accounts.socialSecurityNumber
                    ] =
                        request.socialSecurityNumber
                            .normalizeNullableText()
                }

                if (
                    request.has(
                        "nationalHealthInsuranceNumber"
                    )
                ) {
                    statement[
                        Accounts.nationalHealthInsuranceNumber
                    ] =
                        request.nationalHealthInsuranceNumber
                            .normalizeNullableText()
                }

                if (
                    request.has(
                        "bankName"
                    )
                ) {
                    statement[
                        Accounts.bankName
                    ] =
                        request.bankName
                            .normalizeNullableText()
                }

                if (
                    request.has(
                        "bankAccountBranch"
                    )
                ) {
                    statement[
                        Accounts.bankAccountBranch
                    ] =
                        request.bankAccountBranch
                            .normalizeNullableText()
                }

                if (
                    request.has(
                        "bankAccountNumber"
                    )
                ) {
                    statement[
                        Accounts.bankAccountNumber
                    ] =
                        request.bankAccountNumber
                            .normalizeNullableText()
                }

                if (
                    request.has(
                        "supervisorName"
                    )
                ) {
                    statement[
                        Accounts.supervisorName
                    ] =
                        request.supervisorName
                            .normalizeNullableText()
                }

                if (
                    request.has(
                        "selfAssessmentDescription"
                    )
                ) {
                    statement[
                        Accounts.selfAssessmentDescription
                    ] =
                        request.selfAssessmentDescription
                            .normalizeNullableText()
                }

                /*
                 * Integer fields
                 */

                if (
                    request.has(
                        "standardRetirementAge"
                    )
                ) {
                    request.standardRetirementAge
                        ?.let { retirementAge ->
                            statement[
                                Accounts.standardRetirementAge
                            ] =
                                retirementAge
                        }
                }
                if (
                    request.has(
                        "numberOfTargets"
                    )
                ) {
                    statement[
                        Accounts.numberOfTargets
                    ] =
                        request.numberOfTargets
                }

                if (
                    request.has(
                        "numberOfTargetsMet"
                    )
                ) {
                    statement[
                        Accounts.numberOfTargetsMet
                    ] =
                        request.numberOfTargetsMet
                }

                if (
                    request.has(
                        "numberOfTargetsNotMet"
                    )
                ) {
                    statement[
                        Accounts.numberOfTargetsNotMet
                    ] =
                        request.numberOfTargetsNotMet
                }

                if (
                    request.has(
                        "numberOfFocusAreas"
                    )
                ) {
                    statement[
                        Accounts.numberOfFocusAreas
                    ] =
                        request.numberOfFocusAreas
                }

                /*
                 * Decimal fields
                 */

                if (
                    request.has(
                        "singleSpineMonthlySalary"
                    )
                ) {
                    statement[
                        Accounts.singleSpineMonthlySalary
                    ] =
                        request.singleSpineMonthlySalary
                            .toBigDecimalOrNullSafely()
                }

                if (
                    request.has(
                        "monthlyGrossPay"
                    )
                ) {
                    statement[
                        Accounts.monthlyGrossPay
                    ] =
                        request.monthlyGrossPay
                            .toBigDecimalOrNullSafely()
                }

                if (
                    request.has(
                        "annualSalary"
                    )
                ) {
                    statement[
                        Accounts.annualSalary
                    ] =
                        request.annualSalary
                            .toBigDecimalOrNullSafely()
                }

                if (
                    request.has(
                        "overallAssessmentScore"
                    )
                ) {
                    statement[
                        Accounts.overallAssessmentScore
                    ] =
                        request.overallAssessmentScore
                            .toBigDecimalOrNullSafely()
                }

                /*
                 * Date fields
                 */

                if (
                    request.has(
                        "dateOfBirth"
                    )
                ) {
                    statement[
                        Accounts.dateOfBirth
                    ] =
                        request.dateOfBirth
                            .toLocalDateOrNull()
                }

                if (
                    request.has(
                        "dateOfAssumptionOfDuty"
                    )
                ) {
                    statement[
                        Accounts.dateOfAssumptionOfDuty
                    ] =
                        request.dateOfAssumptionOfDuty
                            .toLocalDateOrNull()
                }

                if (
                    request.has(
                        "substantiveDate"
                    )
                ) {
                    statement[
                        Accounts.substantiveDate
                    ] =
                        request.substantiveDate
                            .toLocalDateOrNull()
                }

                if (
                    request.has(
                        "nationalEffectiveDate"
                    )
                ) {
                    statement[
                        Accounts.nationalEffectiveDate
                    ] =
                        request.nationalEffectiveDate
                            .toLocalDateOrNull()
                }

                if (
                    request.has(
                        "dateOfFirstAppointment"
                    )
                ) {
                    statement[
                        Accounts.dateOfFirstAppointment
                    ] =
                        request.dateOfFirstAppointment
                            .toLocalDateOrNull()
                }

                if (
                    request.has(
                        "dateOfLastPromotion"
                    )
                ) {
                    statement[
                        Accounts.dateOfLastPromotion
                    ] =
                        request.dateOfLastPromotion
                            .toLocalDateOrNull()
                }

                /*
                 * Foreign-key fields
                 *
                 * Exposed v1 places EntityID under:
                 *
                 * org.jetbrains.exposed.v1.core.dao.id.EntityID
                 */

                if (
                    request.has(
                        "currentGradeId"
                    )
                ) {
                    statement[
                        Accounts.currentGradeId
                    ] =
                        request.currentGradeId
                            ?.let { currentGradeId ->
                                EntityID(
                                    currentGradeId,
                                    CurrentGrades
                                )
                            }
                }

                if (
                    request.has(
                        "nextGradeId"
                    )
                ) {
                    statement[
                        Accounts.nextGradeId
                    ] =
                        request.nextGradeId
                            ?.let { nextGradeId ->
                                EntityID(
                                    nextGradeId,
                                    NextGrades
                                )
                            }
                }

                if (
                    request.has(
                        "changeOfGradeId"
                    )
                ) {
                    statement[
                        Accounts.changeOfGradeId
                    ] =
                        request.changeOfGradeId
                            ?.let { changeOfGradeId ->
                                EntityID(
                                    changeOfGradeId,
                                    ChangeOfGrades
                                )
                            }
                }

                if (
                    request.has(
                        "managementUnitCostCentreId"
                    )
                ) {
                    statement[
                        Accounts.managementUnitCostCentreId
                    ] =
                        request.managementUnitCostCentreId
                            ?.let { managementUnitId ->
                                EntityID(
                                    managementUnitId,
                                    ManagementUnits
                                )
                            }
                }

                if (
                    request.has(
                        "directorateId"
                    )
                ) {
                    statement[
                        Accounts.directorateId
                    ] =
                        request.directorateId
                            ?.let { directorateId ->
                                EntityID(
                                    directorateId,
                                    Departments
                                )
                            }
                }

                if (
                    request.has(
                        "categoryId"
                    )
                ) {
                    statement[
                        Accounts.categoryId
                    ] =
                        request.categoryId
                            ?.let { categoryId ->
                                EntityID(
                                    categoryId,
                                    StaffClasses
                                )
                            }
                }

                if (
                    request.has(
                        "districtId"
                    )
                ) {
                    statement[
                        Accounts.districtId
                    ] =
                        request.districtId
                            ?.let { districtId ->
                                EntityID(
                                    districtId,
                                    Districts
                                )
                            }
                }

                if (
                    request.has(
                        "regionId"
                    )
                ) {
                    statement[
                        Accounts.regionId
                    ] =
                        request.regionId
                            ?.let { regionId ->
                                EntityID(
                                    regionId,
                                    Regions
                                )
                            }
                }

                if (
                    request.has(
                        "titleId"
                    )
                ) {
                    statement[
                        Accounts.titleId
                    ] =
                        request.titleId
                            ?.let { titleId ->
                                EntityID(
                                    titleId,
                                    Titles
                                )
                            }
                }

                if (
                    request.has(
                        "onLeaveTypeId"
                    )
                ) {
                    statement[
                        Accounts.onLeaveTypeId
                    ] =
                        request.onLeaveTypeId
                            ?.let { onLeaveTypeId ->
                                EntityID(
                                    onLeaveTypeId,
                                    OnLeaveTypes
                                )
                            }
                }

                if (
                    request.has(
                        "academicQualificationId"
                    )
                ) {
                    statement[
                        Accounts.academicQualificationId
                    ] =
                        request.academicQualificationId
                            ?.let { qualificationId ->
                                EntityID(
                                    qualificationId,
                                    AcademicQualifications
                                )
                            }
                }

                /*
                 * Profile-picture fields
                 */

                if (
                    request.has(
                        "profilePictureUrl"
                    )
                ) {
                    statement[
                        Accounts.profilePictureUrl
                    ] =
                        request.profilePictureUrl
                            .normalizeNullableText()

                    statement[
                        Accounts.profilePicturePublicId
                    ] =
                        request.profilePicturePublicId
                            .normalizeNullableText()
                }
            }

        println(
            "Account update repository completed"
        )

        println(
            "Account ID: $accountId"
        )

        println(
            "Updated rows: $updatedRows"
        )

        println(
            "=================================================="
        )

        return updatedRows > 0
    }

    suspend fun getUpdatedAccount(
        accountId: Int
    ): AccountResponse? {
        println(
            "Retrieving updated account"
        )

        println(
            "Account ID: $accountId"
        )

        val row =
            Accounts
                .selectAll()
                .where {
                    Accounts.id eq
                            accountId
                }
                .firstOrNull()
                ?: run {
                    println(
                        "Updated account was not found"
                    )

                    println(
                        "Account ID: $accountId"
                    )

                    return null
                }

        return AccountRepository
            .rowToAccountResponse(
                row
            )
    }

    private fun AdminUserUpdateRequest.has(
        fieldName: String
    ): Boolean {
        return providedFields.contains(
            fieldName
        )
    }

    private fun String?.normalizeNullableText():
            String? {
        if (this == null) {
            return null
        }

        return trim()
            .takeIf {
                it.isNotEmpty()
            }
    }

    private fun String?.toBigDecimalOrNullSafely() =
        this
            ?.trim()
            ?.replace(
                ",",
                ""
            )
            ?.takeIf {
                it.isNotBlank()
            }
            ?.toBigDecimalOrNull()

    private fun String?.toLocalDateOrNull():
            LocalDate? {
        if (this.isNullOrBlank()) {
            return null
        }

        return runCatching {
            LocalDate.parse(
                trim()
            )
        }.getOrNull()
    }

    private inline fun <
            reified T : Enum<T>
            > parseEnumValue(
        value: String
    ): T? {
        val rawValue =
            value.trim()

        if (rawValue.isBlank()) {
            return null
        }

        val normalizedValue =
            rawValue
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
                                    rawValue,

                                ignoreCase =
                                    true
                            )
            }
    }
}