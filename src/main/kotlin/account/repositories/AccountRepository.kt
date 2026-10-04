package com.hr.account.repositories





import com.hr.academicqualification.table.AcademicQualifications
import com.hr.account.dtos.AccountAcademicQualificationResponse

import com.hr.classes.tables.Classes

import kotlinx.coroutines.flow.firstOrNull

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

import com.hr.account.dtos.AccountCreateRequest
import com.hr.account.dtos.AccountResponse
import com.hr.account.helpers.AccountComputedValues
import com.hr.account.security.AccountPassword
import com.hr.account.table.Accounts
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update
import java.math.BigDecimal
import java.time.LocalDate

object AccountRepository {




    suspend fun rowToAccountResponse(
        row: ResultRow
    ): AccountResponse {
        val accountId =
            row[Accounts.id].value

        val userId =
            row[Accounts.userId]

        val firstName =
            row[Accounts.firstName]

        val middleName =
            row[Accounts.middleName]

        val lastName =
            row[Accounts.lastName]

        val dateOfBirth =
            row[Accounts.dateOfBirth]

        val dateOfLastPromotion =
            row[Accounts.dateOfLastPromotion]

        val dateOfFirstAppointment =
            row[Accounts.dateOfFirstAppointment]

        val retirementAge =
            row[Accounts.standardRetirementAge]

        val academicQualificationId =
            row[Accounts.academicQualificationId]
                ?.value

        val directorateId =
            row[Accounts.directorateId]
                ?.value

        val categoryId =
            row[Accounts.categoryId]
                ?.value

        val districtId =
            row[Accounts.districtId]
                ?.value

        val regionId =
            row[Accounts.regionId]
                ?.value

        val currentGradeId =
            row[Accounts.currentGradeId]
                ?.value

        val nextGradeId =
            row[Accounts.nextGradeId]
                ?.value

        val changeOfGradeId =
            row[Accounts.changeOfGradeId]
                ?.value

        val managementUnitCostCentreId =
            row[
                Accounts.managementUnitCostCentreId
            ]
                ?.value

        val titleId =
            row[Accounts.titleId]
                ?.value

        val onLeaveTypeId =
            row[Accounts.onLeaveTypeId]
                ?.value

        val fullName =
            AccountComputedValues.fullName(
                firstName = firstName,
                middleName = middleName,
                lastName = lastName
            )

        val displayName =
            AccountComputedValues.displayName(
                fullName = fullName,
                userId = userId
            )

        val response =
            AccountResponse(
                id =
                    accountId,

                userId =
                    userId,

                firstName =
                    firstName,

                middleName =
                    middleName,

                lastName =
                    lastName,

                maidenName =
                    row[Accounts.maidenName],

                fullName =
                    fullName,

                displayName =
                    displayName,

                role =
                    row[Accounts.role]
                        ?.name,

                gender =
                    row[Accounts.gender]
                        ?.name,

                maritalStatus =
                    row[Accounts.maritalStatus]
                        ?.name,

                professional =
                    row[Accounts.professional]
                        ?.name,

                professionalQualification =
                    row[
                        Accounts.professionalQualification
                    ],

                staffCategory =
                    row[Accounts.staffCategory]
                        ?.name,

                fulltimeContractStaff =
                    row[
                        Accounts.fulltimeContractStaff
                    ]
                        ?.name,

                isActive =
                    row[Accounts.isActive],

                isStaff =
                    row[Accounts.isStaff],

                isSuperuser =
                    row[Accounts.isSuperuser],

                email =
                    row[Accounts.email],

                phoneNumber =
                    row[Accounts.phoneNumber],

                profilePictureUrl =
                    row[Accounts.profilePictureUrl],

                profilePicturePublicId =
                    row[
                        Accounts.profilePicturePublicId
                    ],

                ghanaCardNumber =
                    row[Accounts.ghanaCardNumber],

                socialSecurityNumber =
                    row[
                        Accounts.socialSecurityNumber
                    ],

                nationalHealthInsuranceNumber =
                    row[
                        Accounts
                            .nationalHealthInsuranceNumber
                    ],

                dateOfBirth =
                    dateOfBirth?.toString(),

                age =
                    AccountComputedValues.age(
                        dateOfBirth
                    ),

                standardRetirementAge =
                    retirementAge,

                dateOfRetirement =
                    AccountComputedValues
                        .dateOfRetirement(
                            dateOfBirth =
                                dateOfBirth,

                            standardRetirementAge =
                                retirementAge
                        )
                        ?.toString(),

                dateOfAssumptionOfDuty =
                    row[
                        Accounts.dateOfAssumptionOfDuty
                    ]
                        ?.toString(),

                substantiveDate =
                    row[Accounts.substantiveDate]
                        ?.toString(),

                nationalEffectiveDate =
                    row[
                        Accounts.nationalEffectiveDate
                    ]
                        ?.toString(),

                dateOfLastPromotion =
                    dateOfLastPromotion
                        ?.toString(),

                dateOfFirstAppointment =
                    dateOfFirstAppointment
                        ?.toString(),

                yearsOnCurrentGrade =
                    AccountComputedValues
                        .yearsOnCurrentGrade(
                            dateOfLastPromotion
                        ),

                numberOfYearsInService =
                    AccountComputedValues
                        .numberOfYearsInService(
                            dateOfFirstAppointment
                        ),

                currentSalaryPoint =
                    row[
                        Accounts.currentSalaryPoint
                    ]
                        ?.name,

                currentSalaryLevel =
                    row[
                        Accounts.currentSalaryLevel
                    ]
                        ?.name,

                nextSalaryLevel =
                    row[Accounts.nextSalaryLevel]
                        ?.name,

                singleSpineMonthlySalary =
                    row[
                        Accounts.singleSpineMonthlySalary
                    ]
                        ?.toPlainString(),

                monthlyGrossPay =
                    row[Accounts.monthlyGrossPay]
                        ?.toPlainString(),

                annualSalary =
                    row[Accounts.annualSalary]
                        ?.toPlainString(),

                numberOfFocusAreas =
                    row[
                        Accounts.numberOfFocusAreas
                    ],

                numberOfTargets =
                    row[Accounts.numberOfTargets],

                numberOfTargetsMet =
                    row[
                        Accounts.numberOfTargetsMet
                    ],

                numberOfTargetsNotMet =
                    row[
                        Accounts.numberOfTargetsNotMet
                    ],

                overallAssessmentScore =
                    row[
                        Accounts.overallAssessmentScore
                    ]
                        ?.toPlainString(),

                selfAssessmentDescription =
                    row[
                        Accounts
                            .selfAssessmentDescription
                    ],

                bankName =
                    row[Accounts.bankName],

                bankAccountNumber =
                    row[
                        Accounts.bankAccountNumber
                    ],

                bankAccountBranch =
                    row[
                        Accounts.bankAccountBranch
                    ],

                payrollStatus =
                    row[Accounts.payrollStatus]
                        ?.name,

                atPostOnLeave =
                    row[Accounts.atPostOnLeave]
                        ?.name,

                accommodationStatus =
                    row[
                        Accounts.accommodationStatus
                    ]
                        ?.name,

                supervisorName =
                    row[Accounts.supervisorName],

                academicQualificationId =
                    academicQualificationId,

                academicQualification =
                    findAcademicQualification(
                        academicQualificationId
                    ),

                directorateId =
                    directorateId,

                directorateName =
                    findDirectorateName(
                        directorateId
                    ),

                categoryId =
                    categoryId,

                categoryName =
                    findCategoryName(
                        categoryId
                    ),

                districtId =
                    districtId,

                districtName =
                    findDistrictName(
                        districtId
                    ),

                regionId =
                    regionId,

                regionName =
                    findRegionName(
                        regionId
                    ),

                currentGradeId =
                    currentGradeId,

                currentGradeName =
                    findCurrentGradeName(
                        currentGradeId
                    ),

                nextGradeId =
                    nextGradeId,

                nextGradeName =
                    findNextGradeName(
                        nextGradeId
                    ),

                changeOfGradeId =
                    changeOfGradeId,

                changeOfGradeName =
                    findChangeOfGradeName(
                        changeOfGradeId
                    ),

                managementUnitCostCentreId =
                    managementUnitCostCentreId,

                managementUnitCostCentreName =
                    findManagementUnitName(
                        managementUnitCostCentreId
                    ),

                titleId =
                    titleId,

                titleName =
                    findTitleName(
                        titleId
                    ),

                onLeaveTypeId =
                    onLeaveTypeId,

                onLeaveTypeName =
                    findOnLeaveTypeName(
                        onLeaveTypeId
                    ),

                dateJoined =
                    row[Accounts.dateJoined]
                        .toString(),

                lastLogin =
                    row[Accounts.lastLogin]
                        ?.toString()
            )

        println(
            "Account response created: " +
                    "id=${response.id}, " +
                    "userId=${response.userId}, " +
                    "fullName=${response.fullName}, " +
                    "role=${response.role}, " +
                    "region=${response.regionName}, " +
                    "district=${response.districtName}, " +
                    "directorate=${response.directorateName}, " +
                    "category=${response.categoryName}, " +
                    "currentGrade=${response.currentGradeName}, " +
                    "nextGrade=${response.nextGradeName}, " +
                    "managementUnit=${response.managementUnitCostCentreName}, " +
                    "academicQualification=${response.academicQualification?.name}"
        )

        return response
    }

    private suspend fun findAcademicQualification(
        academicQualificationId: Int?
    ): AccountAcademicQualificationResponse? {
        if (academicQualificationId == null) {
            return null
        }

        val row =
            AcademicQualifications
                .selectAll()
                .where {
                    AcademicQualifications.id eq
                            academicQualificationId
                }
                .firstOrNull()
                ?: return null

        return AccountAcademicQualificationResponse(
            id =
                row[
                    AcademicQualifications.id
                ].value,

            name =
                row[
                    AcademicQualifications.name
                ]
        )
    }

    private suspend fun findDirectorateName(
        directorateId: Int?
    ): String? {
        if (directorateId == null) {
            return null
        }

        return Departments
            .selectAll()
            .where {
                Departments.id eq
                        directorateId
            }
            .firstOrNull()
            ?.get(
                Departments.departmentName
            )
    }


    private suspend fun findCategoryName(
        categoryId: Int?
    ): String? {
        if (categoryId == null) {
            return null
        }

        return Classes
            .selectAll()
            .where {
                Classes.id eq
                        categoryId
            }
            .firstOrNull()
            ?.get(
                Classes.classesName
            )
    }

    private suspend fun findDistrictName(
        districtId: Int?
    ): String? {
        if (districtId == null) {
            return null
        }

        return Districts
            .selectAll()
            .where {
                Districts.id eq
                        districtId
            }
            .firstOrNull()
            ?.get(
                Districts.district
            )
    }

    private suspend fun findRegionName(
        regionId: Int?
    ): String? {
        if (regionId == null) {
            return null
        }

        return Regions
            .selectAll()
            .where {
                Regions.id eq
                        regionId
            }
            .firstOrNull()
            ?.get(
                Regions.region
            )
    }


    private suspend fun findCurrentGradeName(
        currentGradeId: Int?
    ): String? {
        if (currentGradeId == null) {
            return null
        }

        return CurrentGrades
            .selectAll()
            .where {
                CurrentGrades.id eq
                        currentGradeId
            }
            .firstOrNull()
            ?.get(
                CurrentGrades.currentGrade
            )
    }



    private suspend fun findNextGradeName(
        nextGradeId: Int?
    ): String? {
        if (nextGradeId == null) {
            return null
        }

        return NextGrades
            .selectAll()
            .where {
                NextGrades.id eq
                        nextGradeId
            }
            .firstOrNull()
            ?.get(
                NextGrades.nextGrade
            )
    }


    private suspend fun findChangeOfGradeName(
        changeOfGradeId: Int?
    ): String? {
        if (changeOfGradeId == null) {
            return null
        }

        return ChangeOfGrades
            .selectAll()
            .where {
                ChangeOfGrades.id eq
                        changeOfGradeId
            }
            .firstOrNull()
            ?.get(
                ChangeOfGrades.grade
            )
    }



    private suspend fun findManagementUnitName(
        managementUnitId: Int?
    ): String? {
        if (managementUnitId == null) {
            return null
        }

        return ManagementUnits
            .selectAll()
            .where {
                ManagementUnits.id eq
                        managementUnitId
            }
            .firstOrNull()
            ?.get(
                ManagementUnits.managementUnitName
            )
    }


    private suspend fun findTitleName(
        titleId: Int?
    ): String? {
        if (titleId == null) {
            return null
        }

        return Titles
            .selectAll()
            .where {
                Titles.id eq
                        titleId
            }
            .firstOrNull()
            ?.get(
                Titles.title
            )
    }

    private suspend fun findOnLeaveTypeName(
        onLeaveTypeId: Int?
    ): String? {
        if (onLeaveTypeId == null) {
            return null
        }

        return OnLeaveTypes
            .selectAll()
            .where {
                OnLeaveTypes.id eq
                        onLeaveTypeId
            }
            .firstOrNull()
            ?.get(
                OnLeaveTypes.name
            )
    }




















    suspend fun userIdExists(
        userId: String,
        excludeAccountId: Int? = null
    ): Boolean {
        val normalizedUserId =
            userId.trim()

        if (normalizedUserId.isBlank()) {
            return false
        }

        return Accounts
            .selectAll()
            .where {
                if (excludeAccountId == null) {
                    Accounts.userId eq normalizedUserId
                } else {
                    (
                            Accounts.userId eq normalizedUserId
                            ) and (
                            Accounts.id neq excludeAccountId
                            )
                }
            }
            .singleOrNull() != null
    }

    suspend fun phoneNumberExists(
        phoneNumber: String,
        excludeAccountId: Int? = null
    ): Boolean {
        val normalizedPhoneNumber =
            phoneNumber.trim()

        if (normalizedPhoneNumber.isBlank()) {
            return false
        }

        return Accounts
            .selectAll()
            .where {
                if (excludeAccountId == null) {
                    Accounts.phoneNumber eq normalizedPhoneNumber
                } else {
                    (
                            Accounts.phoneNumber eq normalizedPhoneNumber
                            ) and (
                            Accounts.id neq excludeAccountId
                            )
                }
            }
            .singleOrNull() != null
    }






    suspend fun getAll(): List<AccountResponse> {

        return Accounts
            .selectAll()
            .orderBy(
                Accounts.id,
                SortOrder.DESC
            )
            .map { row ->
                rowToAccountResponse(row)
            }
            .toList()
    }

    suspend fun update(
        id: Int,
        request: AccountCreateRequest
    ): Boolean {

        return Accounts.update(
            where = {
                Accounts.id eq id
            }
        ) {

            it[Accounts.userId] =
                request.userId.trim()

            it[Accounts.role] =
                request.role

            it[Accounts.firstName] =
                request.firstName?.trim()

            it[Accounts.middleName] =
                request.middleName?.trim()

            it[Accounts.lastName] =
                request.lastName?.trim()

            it[Accounts.maidenName] =
                request.maidenName?.trim()

            it[Accounts.gender] =
                request.gender

            it[Accounts.dateOfBirth] =
                parseDate(request.dateOfBirth)

            it[Accounts.maritalStatus] =
                request.maritalStatus

            it[Accounts.academicQualificationId] =
                request.academicQualificationId?.let { qualificationId ->
                    EntityID(
                        qualificationId,
                        AcademicQualifications
                    )
                }

            it[Accounts.directorateId] =
                request.directorateId?.let { directorateId ->
                    EntityID(
                        directorateId,
                        Departments
                    )
                }

            it[Accounts.categoryId] =
                request.categoryId?.let { categoryId ->
                    EntityID(
                        categoryId,
                        StaffClasses
                    )
                }

            it[Accounts.districtId] =
                request.districtId?.let { districtId ->
                    EntityID(
                        districtId,
                        Districts
                    )
                }

            it[Accounts.regionId] =
                request.regionId?.let { regionId ->
                    EntityID(
                        regionId,
                        Regions
                    )
                }

            it[Accounts.currentGradeId] =
                request.currentGradeId?.let { currentGradeId ->
                    EntityID(
                        currentGradeId,
                        CurrentGrades
                    )
                }

            it[Accounts.nextGradeId] =
                request.nextGradeId?.let { nextGradeId ->
                    EntityID(
                        nextGradeId,
                        NextGrades
                    )
                }

            it[Accounts.changeOfGradeId] =
                request.changeOfGradeId?.let { changeOfGradeId ->
                    EntityID(
                        changeOfGradeId,
                        ChangeOfGrades
                    )
                }

            it[Accounts.managementUnitCostCentreId] =
                request.managementUnitCostCentreId?.let { managementUnitId ->
                    EntityID(
                        managementUnitId,
                        ManagementUnits
                    )
                }

            it[Accounts.titleId] =
                request.titleId?.let { titleId ->
                    EntityID(
                        titleId,
                        Titles
                    )
                }

            it[Accounts.onLeaveTypeId] =
                request.onLeaveTypeId?.let { onLeaveTypeId ->
                    EntityID(
                        onLeaveTypeId,
                        OnLeaveTypes
                    )
                }

            it[Accounts.professional] =
                request.professional

            it[Accounts.professionalQualification] =
                request.professionalQualification?.trim()

            it[Accounts.staffCategory] =
                request.staffCategory

            it[Accounts.fulltimeContractStaff] =
                request.fulltimeContractStaff

            it[Accounts.currentSalaryLevel] =
                request.currentSalaryLevel

            it[Accounts.currentSalaryPoint] =
                request.currentSalaryPoint

            it[Accounts.nextSalaryLevel] =
                request.nextSalaryLevel

            it[Accounts.dateOfAssumptionOfDuty] =
                parseDate(request.dateOfAssumptionOfDuty)

            it[Accounts.substantiveDate] =
                parseDate(request.substantiveDate)

            it[Accounts.nationalEffectiveDate] =
                parseDate(request.nationalEffectiveDate)

            it[Accounts.dateOfLastPromotion] =
                parseDate(request.dateOfLastPromotion)

            it[Accounts.dateOfFirstAppointment] =
                parseDate(request.dateOfFirstAppointment)

            it[Accounts.singleSpineMonthlySalary] =
                parseDecimal(request.singleSpineMonthlySalary)

            it[Accounts.monthlyGrossPay] =
                parseDecimal(request.monthlyGrossPay)

            it[Accounts.annualSalary] =
                parseDecimal(request.annualSalary)

            it[Accounts.numberOfFocusAreas] =
                request.numberOfFocusAreas

            it[Accounts.numberOfTargets] =
                request.numberOfTargets

            it[Accounts.numberOfTargetsMet] =
                request.numberOfTargetsMet

            it[Accounts.numberOfTargetsNotMet] =
                request.numberOfTargetsNotMet

            it[Accounts.overallAssessmentScore] =
                parseDecimal(request.overallAssessmentScore)

            it[Accounts.selfAssessmentDescription] =
                request.selfAssessmentDescription?.trim()

            it[Accounts.phoneNumber] =
                request.phoneNumber?.trim()

            it[Accounts.ghanaCardNumber] =
                request.ghanaCardNumber?.trim()

            it[Accounts.socialSecurityNumber] =
                request.socialSecurityNumber?.trim()

            it[Accounts.nationalHealthInsuranceNumber] =
                request.nationalHealthInsuranceNumber?.trim()

            it[Accounts.bankName] =
                request.bankName?.trim()

            it[Accounts.bankAccountNumber] =
                request.bankAccountNumber?.trim()

            it[Accounts.bankAccountBranch] =
                request.bankAccountBranch?.trim()

            it[Accounts.payrollStatus] =
                request.payrollStatus

            it[Accounts.atPostOnLeave] =
                request.atPostOnLeave

            it[Accounts.accommodationStatus] =
                request.accommodationStatus

            it[Accounts.supervisorName] =
                request.supervisorName?.trim()

            it[Accounts.email] =
                request.email?.trim()

            it[Accounts.profilePictureUrl] =
                request.profilePictureUrl?.trim()

            it[Accounts.profilePicturePublicId] =
                request.profilePicturePublicId?.trim()

            it[Accounts.standardRetirementAge] =
                request.standardRetirementAge

            it[Accounts.isActive] =
                request.isActive

            it[Accounts.isStaff] =
                request.isStaff

            it[Accounts.isSuperuser] =
                request.isSuperuser
        } > 0
    }

    suspend fun delete(
        id: Int
    ): Boolean {

        return Accounts.deleteWhere {
            Accounts.id eq id
        } > 0
    }














    suspend fun create(
        request: AccountCreateRequest
    ): Int {
        val passwordHash =
            AccountPassword.hashDefaultPassword()

        return Accounts
            .insertAndGetId {
                /*
                 * Login and authentication
                 */

                it[Accounts.userId]=
                    request.userId.trim()

                it[Accounts.passwordHash] =
                    passwordHash

                it[Accounts.role] =
                    request.role

                it[Accounts.isActive] =
                    request.isActive

                it[Accounts.isStaff] =
                    request.isStaff

                it[Accounts.isSuperuser] =
                    request.isSuperuser

                /*
                 * Personal information
                 */

                it[Accounts.firstName] =
                    cleanNullableString(
                        request.firstName
                    )

                it[Accounts.middleName] =
                    cleanNullableString(
                        request.middleName
                    )

                it[Accounts.lastName] =
                    cleanNullableString(
                        request.lastName
                    )

                it[Accounts.maidenName] =
                    cleanNullableString(
                        request.maidenName
                    )

                it[Accounts.gender] =
                    request.gender

                it[Accounts.dateOfBirth] =
                    parseDate(
                        request.dateOfBirth
                    )

                it[Accounts.maritalStatus] =
                    request.maritalStatus

                it[Accounts.standardRetirementAge] =
                    request.standardRetirementAge

                /*
                 * Academic qualification
                 */

                it[Accounts.academicQualificationId] =
                    request.academicQualificationId
                        ?.let { academicQualificationId ->
                            EntityID(
                                academicQualificationId,
                                AcademicQualifications
                            )
                        }

                /*
                 * Organization relationships
                 */

                it[Accounts.directorateId] =
                    request.directorateId
                        ?.let { directorateId ->
                            EntityID(
                                directorateId,
                                Departments
                            )
                        }

                it[Accounts.categoryId] =
                    request.categoryId
                        ?.let { categoryId ->
                            EntityID(
                                categoryId,
                                StaffClasses
                            )
                        }

                it[Accounts.districtId] =
                    request.districtId
                        ?.let { districtId ->
                            EntityID(
                                districtId,
                                Districts
                            )
                        }

                it[Accounts.regionId] =
                    request.regionId
                        ?.let { regionId ->
                            EntityID(
                                regionId,
                                Regions
                            )
                        }

                it[Accounts.currentGradeId] =
                    request.currentGradeId
                        ?.let { currentGradeId ->
                            EntityID(
                                currentGradeId,
                                CurrentGrades
                            )
                        }

                it[Accounts.nextGradeId] =
                    request.nextGradeId
                        ?.let { nextGradeId ->
                            EntityID(
                                nextGradeId,
                                NextGrades
                            )
                        }

                it[Accounts.changeOfGradeId] =
                    request.changeOfGradeId
                        ?.let { changeOfGradeId ->
                            EntityID(
                                changeOfGradeId,
                                ChangeOfGrades
                            )
                        }

                it[Accounts.managementUnitCostCentreId] =
                    request.managementUnitCostCentreId
                        ?.let { managementUnitId ->
                            EntityID(
                                managementUnitId,
                                ManagementUnits
                            )
                        }

                it[Accounts.titleId] =
                    request.titleId
                        ?.let { titleId ->
                            EntityID(
                                titleId,
                                Titles
                            )
                        }

                it[Accounts.onLeaveTypeId] =
                    request.onLeaveTypeId
                        ?.let { onLeaveTypeId ->
                            EntityID(
                                onLeaveTypeId,
                                OnLeaveTypes
                            )
                        }

                /*
                 * Employment and professional information
                 */

                it[Accounts.professional] =
                    request.professional

                it[Accounts.professionalQualification] =
                    cleanNullableString(
                        request.professionalQualification
                    )

                it[Accounts.staffCategory] =
                    request.staffCategory

                it[Accounts.fulltimeContractStaff] =
                    request.fulltimeContractStaff

                /*
                 * Salary grade information
                 */

                it[Accounts.currentSalaryLevel] =
                    request.currentSalaryLevel

                it[Accounts.currentSalaryPoint] =
                    request.currentSalaryPoint

                it[Accounts.nextSalaryLevel] =
                    request.nextSalaryLevel

                /*
                 * Employment dates
                 */

                it[Accounts.dateOfAssumptionOfDuty] =
                    parseDate(
                        request.dateOfAssumptionOfDuty
                    )

                it[Accounts.substantiveDate] =
                    parseDate(
                        request.substantiveDate
                    )

                it[Accounts.nationalEffectiveDate] =
                    parseDate(
                        request.nationalEffectiveDate
                    )

                it[Accounts.dateOfLastPromotion] =
                    parseDate(
                        request.dateOfLastPromotion
                    )

                it[Accounts.dateOfFirstAppointment] =
                    parseDate(
                        request.dateOfFirstAppointment
                    )

                /*
                 * Salary information
                 */

                it[Accounts.singleSpineMonthlySalary] =
                    parseDecimal(
                        request.singleSpineMonthlySalary
                    )

                it[Accounts.monthlyGrossPay] =
                    parseDecimal(
                        request.monthlyGrossPay
                    )

                it[Accounts.annualSalary] =
                    parseDecimal(
                        request.annualSalary
                    )

                /*
                 * Performance information
                 */

                it[Accounts.numberOfFocusAreas] =
                    request.numberOfFocusAreas

                it[Accounts.numberOfTargets] =
                    request.numberOfTargets

                it[Accounts.numberOfTargetsMet] =
                    request.numberOfTargetsMet

                it[Accounts.numberOfTargetsNotMet] =
                    request.numberOfTargetsNotMet

                it[Accounts.overallAssessmentScore] =
                    parseDecimal(
                        request.overallAssessmentScore
                    )

                it[Accounts.selfAssessmentDescription] =
                    cleanNullableString(
                        request.selfAssessmentDescription
                    )

                /*
                 * Contact and identification information
                 *
                 * Blank unique values must be stored as null,
                 * not as empty strings.
                 */

                it[Accounts.phoneNumber] =
                    cleanNullableString(
                        request.phoneNumber
                    )

                it[Accounts.ghanaCardNumber] =
                    cleanNullableString(
                        request.ghanaCardNumber
                    )

                it[Accounts.socialSecurityNumber] =
                    cleanNullableString(
                        request.socialSecurityNumber
                    )

                it[Accounts.nationalHealthInsuranceNumber] =
                    cleanNullableString(
                        request.nationalHealthInsuranceNumber
                    )

                /*
                 * Banking information
                 */

                it[Accounts.bankName] =
                    cleanNullableString(
                        request.bankName
                    )

                it[Accounts.bankAccountNumber] =
                    cleanNullableString(
                        request.bankAccountNumber
                    )

                it[Accounts.bankAccountBranch] =
                    cleanNullableString(
                        request.bankAccountBranch
                    )

                /*
                 * Payroll, leave, and accommodation
                 */

                it[Accounts.payrollStatus] =
                    request.payrollStatus

                it[Accounts.atPostOnLeave] =
                    request.atPostOnLeave

                it[Accounts.accommodationStatus] =
                    request.accommodationStatus

                it[Accounts.supervisorName] =
                    cleanNullableString(
                        request.supervisorName
                    )

                it[Accounts.email] =
                    cleanNullableString(
                        request.email
                    )

                /*
                 * Profile picture
                 */

                it[Accounts.profilePictureUrl] =
                    cleanNullableString(
                        request.profilePictureUrl
                    )

                it[Accounts.profilePicturePublicId] =
                    cleanNullableString(
                        request.profilePicturePublicId
                    )
            }
            .value
    }







    suspend fun getById(
        id: Int
    ): AccountResponse? {
        println(
            "Retrieving account details: accountId=$id"
        )

        val row =
            Accounts
                .selectAll()
                .where {
                    Accounts.id eq id
                }
                .singleOrNull()

        if (row == null) {
            println(
                "Account not found: accountId=$id"
            )

            return null
        }

        val account =
            rowToAccountResponse(row)

        println(
            """
        Account details retrieved:
        Account ID: ${account.id}
        User ID: ${account.userId}
        Full name: ${account.fullName}
        Role: ${account.role}
        Active: ${account.isActive}
        Superuser: ${account.isSuperuser}
        Region: ${account.regionName ?: "Not specified"}
        District: ${account.districtName ?: "Not specified"}
        Directorate: ${account.directorateName ?: "Not specified"}
        Class: ${account.categoryName ?: "Not specified"}
        Current grade: ${account.currentGradeName ?: "Not specified"}
        Next grade: ${account.nextGradeName ?: "Not specified"}
        Change of grade: ${account.changeOfGradeName ?: "Not specified"}
        Management unit: ${account.managementUnitCostCentreName ?: "Not specified"}
        Academic qualification: ${account.academicQualification?.name ?: "Not specified"}
        Title: ${account.titleName ?: "Not specified"}
        Leave type: ${account.onLeaveTypeName ?: "Not specified"}
        """.trimIndent()
        )

        return account
    }

    private fun cleanNullableString(
        value: String?
    ): String? {
        return value
            ?.trim()
            ?.takeIf {
                it.isNotBlank()
            }
    }


    private fun parseDate(
        value: String?
    ): LocalDate? {
        return value
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?.let {
                LocalDate.parse(it)
            }
    }

    private fun parseDecimal(
        value: String?
    ): BigDecimal? {
        return value
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?.toBigDecimal()
    }

}




