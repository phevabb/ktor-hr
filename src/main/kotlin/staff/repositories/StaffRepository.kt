package com.hr.staff.repositories




import com.hr.academicqualification.table.AcademicQualifications
import com.hr.account.dtos.Role
import com.hr.account.table.Accounts
import com.hr.changeofgrade.tables.ChangeOfGrades
import com.hr.currentgrade.tables.CurrentGrades
import com.hr.department.table.Departments
import com.hr.districts.tables.Districts
import com.hr.managementUnits.tables.ManagementUnits
import com.hr.nextgrade.tables.NextGrades
import com.hr.onleavetype.table.OnLeaveTypes
import com.hr.region.tables.Regions
import com.hr.staff.dtos.StaffAcademicQualificationResponse
import com.hr.staff.dtos.StaffDetailResponse
import com.hr.staff.dtos.StaffSummaryResponse
import com.hr.staffclass.table.StaffClasses
import com.hr.title.table.Titles
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object StaffRepository {



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


    private suspend fun findTitleName(
        titleId: Int?
    ): String? {
        if (titleId == null) {
            return null
        }

        return Titles
            .selectAll()
            .where {
                Titles.id eq titleId
            }
            .firstOrNull()
            ?.get(
                Titles.title
            )
    }



    private suspend fun findCategoryName(
        categoryId: Int?
    ): String? {
        if (categoryId == null) {
            return null
        }

        return StaffClasses
            .selectAll()
            .where {
                StaffClasses.id eq categoryId
            }
            .firstOrNull()
            ?.get(
                StaffClasses.name
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



    private suspend fun findNextGradeName(
        nextGradeId: Int?
    ): String? {
        if (nextGradeId == null) {
            return null
        }

        return NextGrades
            .selectAll()
            .where {
                NextGrades.id eq nextGradeId
            }
            .firstOrNull()
            ?.get(
                NextGrades.nextGrade
            )
    }


    suspend fun getAll():
            List<StaffSummaryResponse> {
        return Accounts
            .selectAll()
            .where {
                Accounts.role eq Role.Staff
            }
            .orderBy(
                Accounts.id,
                SortOrder.DESC
            )
            .map { row ->
                rowToSummary(row)
            }
            .toList()
    }

    suspend fun getByRegionId(
        regionId: Int
    ): List<StaffSummaryResponse> {
        return Accounts
            .selectAll()
            .where {
                (Accounts.role eq Role.Staff) and
                        (Accounts.regionId eq regionId)
            }
            .orderBy(
                Accounts.id,
                SortOrder.DESC
            )
            .map { row ->
                rowToSummary(row)
            }
            .toList()
    }

    suspend fun getSummaryById(
        staffId: Int
    ): StaffSummaryResponse? {
        val row =
            Accounts
                .selectAll()
                .where {
                    (Accounts.id eq staffId) and
                            (Accounts.role eq Role.Staff)
                }
                .firstOrNull()
                ?: return null

        return rowToSummary(row)
    }

    suspend fun getById(
        staffId: Int
    ): StaffDetailResponse? {
        println(
            "Retrieving staff details: staffId=$staffId"
        )

        val row =
            Accounts
                .selectAll()
                .where {
                    (Accounts.id eq staffId) and
                            (Accounts.role eq Role.Staff)
                }
                .firstOrNull()

        if (row == null) {
            println(
                "Staff account not found: staffId=$staffId"
            )

            return null
        }

        val staff =
            rowToDetail(row)

        println(
            "Staff details retrieved: $staff"
        )

        return staff
    }




    suspend fun exists(
        staffId: Int
    ): Boolean {
        return Accounts
            .selectAll()
            .where {
                (Accounts.id eq staffId) and
                        (Accounts.role eq Role.Staff)
            }
            .firstOrNull() != null
    }

    private suspend fun rowToSummary(
        row: ResultRow
    ): StaffSummaryResponse {
        val accountId =
            row[Accounts.id].value

        val userId =
            row[Accounts.userId]

        val fullName =
            buildFullName(row)

        return StaffSummaryResponse(
            id =
                accountId,

            userId =
                userId,

            fullName =
                fullName,

            displayName =
                buildDisplayName(
                    accountId = accountId,
                    fullName = fullName,
                    userId = userId
                ),

            role =
                row[Accounts.role]
                    ?.toString(),

            profilePictureUrl =
                row[
                    Accounts.profilePictureUrl
                ],

            phoneNumber =
                row[Accounts.phoneNumber],

            isActive =
                row[Accounts.isActive],

            isStaff =
                row[Accounts.isStaff],

            isSuperuser =
                row[Accounts.isSuperuser],

            regionId =
                row[Accounts.regionId]
                    ?.value,

            regionName =
                findRegionName(
                    row[Accounts.regionId]
                        ?.value
                ),

            districtId =
                row[Accounts.districtId]
                    ?.value,

            districtName =
                findDistrictName(
                    row[Accounts.districtId]
                        ?.value
                ),

            directorateId =
                row[Accounts.directorateId]
                    ?.value,

            directorateName =
                findDirectorateName(
                    row[Accounts.directorateId]
                        ?.value
                ),

            currentGradeId =
                row[Accounts.currentGradeId]
                    ?.value,

            currentGradeName =
                findCurrentGradeName(
                    row[Accounts.currentGradeId]
                        ?.value
                )
        )
    }

    private suspend fun rowToDetail(
        row: ResultRow
    ): StaffDetailResponse {
        val accountId =
            row[Accounts.id].value

        val userId =
            row[Accounts.userId]

        val fullName =
            buildFullName(row)

        val academicQualificationId =
            row[
                Accounts.academicQualificationId
            ]
                ?.value

        return StaffDetailResponse(
            id =
                accountId,

            userId =
                userId,

            firstName =
                row[Accounts.firstName],

            middleName =
                row[Accounts.middleName],

            lastName =
                row[Accounts.lastName],

            maidenName =
                row[Accounts.maidenName],

            fullName =
                fullName,

            displayName =
                buildDisplayName(
                    accountId = accountId,
                    fullName = fullName,
                    userId = userId
                ),

            role =
                row[Accounts.role]
                    ?.toString(),

            gender =
                row[Accounts.gender]
                    ?.toString(),

            maritalStatus =
                row[Accounts.maritalStatus]
                    ?.toString(),

            professional =
                row[Accounts.professional]
                    ?.toString(),

            professionalQualification =
                row[
                    Accounts.professionalQualification
                ],

            staffCategory =
                row[Accounts.staffCategory]
                    ?.toString(),

            fulltimeContractStaff =
                row[
                    Accounts.fulltimeContractStaff
                ]
                    ?.toString(),

            isActive =
                row[Accounts.isActive],

            isStaff =
                row[Accounts.isStaff],

            isSuperuser =
                row[Accounts.isSuperuser],

            profilePictureUrl =
                row[
                    Accounts.profilePictureUrl
                ],

            profilePicturePublicId =
                row[
                    Accounts.profilePicturePublicId
                ],

            phoneNumber =
                row[Accounts.phoneNumber],

            ghanaCardNumber =
                row[
                    Accounts.ghanaCardNumber
                ],

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
                row[Accounts.dateOfBirth]
                    ?.toString(),

            standardRetirementAge =
                row[
                    Accounts.standardRetirementAge
                ],

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
                row[
                    Accounts.dateOfLastPromotion
                ]
                    ?.toString(),

            dateOfFirstAppointment =
                row[
                    Accounts.dateOfFirstAppointment
                ]
                    ?.toString(),

            currentSalaryPoint =
                row[
                    Accounts.currentSalaryPoint
                ]
                    ?.toString(),

            currentSalaryLevel =
                row[
                    Accounts.currentSalaryLevel
                ]
                    ?.toString(),

            nextSalaryLevel =
                row[Accounts.nextSalaryLevel]
                    ?.toString(),

            singleSpineMonthlySalary =
                row[
                    Accounts
                        .singleSpineMonthlySalary
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
                    ?.toString(),

            accommodationStatus =
                row[
                    Accounts.accommodationStatus
                ]
                    ?.toString(),

            supervisorName =
                row[Accounts.supervisorName],

            email =
                row[Accounts.email],

            atPostOnLeave =
                row[Accounts.atPostOnLeave]
                    ?.toString(),

            dateJoined =
                row[Accounts.dateJoined]
                    .toString(),

            lastLogin =
                row[Accounts.lastLogin]
                    ?.toString(),

            regionId =
                row[Accounts.regionId]
                    ?.value,

            regionName =
                findRegionName(
                    row[Accounts.regionId]
                        ?.value
                ),

            districtId =
                row[Accounts.districtId]
                    ?.value,

            districtName =
                findDistrictName(
                    row[Accounts.districtId]
                        ?.value
                ),

            directorateId =
                row[Accounts.directorateId]
                    ?.value,

            directorateName =
                findDirectorateName(
                    row[Accounts.directorateId]
                        ?.value
                ),

            currentGradeId =
                row[Accounts.currentGradeId]
                    ?.value,

            currentGradeName =
                findCurrentGradeName(
                    row[Accounts.currentGradeId]
                        ?.value
                ),

            nextGradeId =
                row[Accounts.nextGradeId]
                    ?.value,









            nextGradeName =
                findNextGradeName(
                    row[Accounts.nextGradeId]
                        ?.value
                ),

            changeOfGradeId =
                row[Accounts.changeOfGradeId]
                    ?.value,

            changeOfGradeName =
                findChangeOfGradeName(
                    row[Accounts.changeOfGradeId]
                        ?.value
                ),

            managementUnitCostCentreId =
                row[
                    Accounts.managementUnitCostCentreId
                ]
                    ?.value,

            managementUnitCostCentreName =
                findManagementUnitName(
                    row[
                        Accounts.managementUnitCostCentreId
                    ]
                        ?.value
                ),

            categoryId =
                row[Accounts.categoryId]
                    ?.value,

            categoryName =
                findCategoryName(
                    row[Accounts.categoryId]
                        ?.value
                ),

            titleId =
                row[Accounts.titleId]
                    ?.value,

            titleName =
                findTitleName(
                    row[Accounts.titleId]
                        ?.value
                ),

            onLeaveTypeId =
                row[Accounts.onLeaveTypeId]
                    ?.value,

            onLeaveTypeName =
                findOnLeaveTypeName(
                    row[Accounts.onLeaveTypeId]
                        ?.value
                ),

            academicQualification =
                findAcademicQualification(
                    academicQualificationId
                ),
















        )
    }

    private fun buildFullName(
        row: ResultRow
    ): String {
        return listOfNotNull(
            row[Accounts.firstName],
            row[Accounts.middleName],
            row[Accounts.lastName]
        )
            .map { name ->
                name.trim()
            }
            .filter { name ->
                name.isNotBlank()
            }
            .joinToString(" ")
    }

    private fun buildDisplayName(
        accountId: Int,
        fullName: String,
        userId: String?
    ): String {
        return when {
            fullName.isNotBlank() &&
                    !userId.isNullOrBlank() -> {
                "$fullName - $userId"
            }

            fullName.isNotBlank() -> {
                fullName
            }

            !userId.isNullOrBlank() -> {
                userId
            }

            else -> {
                "Staff #$accountId"
            }
        }
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
                Regions.id eq regionId
            }
            .firstOrNull()
            ?.get(Regions.region)
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
                Districts.id eq districtId
            }
            .firstOrNull()
            ?.get(Districts.district)
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

    private suspend fun findAcademicQualification(
        academicQualificationId: Int?
    ): StaffAcademicQualificationResponse? {
        if (
            academicQualificationId == null
        ) {
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

        return StaffAcademicQualificationResponse(
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
}