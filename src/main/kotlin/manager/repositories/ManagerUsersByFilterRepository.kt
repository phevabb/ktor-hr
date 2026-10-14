package com.hr.manager.repositories


import com.hr.account.dtos.Gender
import com.hr.account.dtos.AccountResponse
import com.hr.account.dtos.FulltimeContractStaff
import com.hr.account.dtos.Professional
import com.hr.account.dtos.SalaryPoint
import com.hr.account.dtos.StaffCategory
import com.hr.account.repositories.AccountRepository
import com.hr.account.table.Accounts
import com.hr.classes.tables.Classes
import com.hr.department.table.Departments
import com.hr.managementUnits.tables.ManagementUnits
import com.hr.manager.dtos.ManagerFilteredUserResponse
import com.hr.onleavetype.table.OnLeaveTypes
import java.util.Locale
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagerUsersByFilterRepository {




    private fun professionalValuesMatch(
        actualValue: String?,
        requestedValue: String
    ): Boolean {
        val normalizedActualValue =
            normalizeProfessionalValue(
                actualValue
            )

        val normalizedRequestedValue =
            normalizeProfessionalValue(
                requestedValue
            )

        return (
                normalizedActualValue.isNotBlank() &&
                        normalizedActualValue ==
                        normalizedRequestedValue
                )
    }

    private fun normalizeProfessionalValue(
        value: String?
    ): String {
        return normalizeValue(
            value
        )
            .replace(
                oldValue =
                    "SUBPROFESSIONAL",

                newValue =
                    "SUB PROFESSIONAL"
            )
    }




    private val ageRanges =
        linkedMapOf(
            "20 - 30" to
                    20..30,

            "31 - 40" to
                    31..40,

            "41 - 50" to
                    41..50,

            "51 - 60" to
                    51..60
        )




    // here
    suspend fun getUsersByFilter(
        regionScopeId: Int?,
        requestedFilter: String
    ): ManagerUsersByFilterRepositoryResult {
        val repositoryStartedAt =
            System.currentTimeMillis()

        println(
            "=================================================="
        )

        println(
            "Manager filtered-users repository started"
        )

        println(
            "Manager region ID: ${regionScopeId ?: "ALL REGIONS"}"
        )

        println(
            "Requested filter: $requestedFilter"
        )

        val normalizedFilter =
            normalizeValue(
                requestedFilter
            )

        if (normalizedFilter.isBlank()) {
            println(
                "Requested filter is blank"
            )

            println(
                "Manager repository duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterRepositoryResult(
                filterType =
                    null,

                totalCount =
                    0L,

                users =
                    emptyList()
            )
        }

        /*
         * Direct class-filter lookup.
         *
         * Resolve the requested class once, then filter
         * Accounts directly using categoryId and regionId.
         */
        val classLookupStartedAt =
            System.currentTimeMillis()

        val matchingClassId =
            Classes
                .selectAll()
                .toList()
                .firstOrNull { row ->
                    normalizeValue(
                        row[
                            Classes.classesName
                        ]
                    ) == normalizedFilter
                }
                ?.get(
                    Classes.id
                )

        println(
            "Manager class lookup duration: ${
                System.currentTimeMillis() -
                        classLookupStartedAt
            } ms"
        )

        if (matchingClassId != null) {
            val classQueryStartedAt =
                System.currentTimeMillis()

            val matchingRows =
                if (regionScopeId == null) {
                    Accounts
                        .selectAll()
                        .where {
                            (
                                    Accounts.isActive eq
                                            true
                                    ) and (
                                    Accounts.categoryId eq
                                            matchingClassId
                                    )
                        }
                        .orderBy(
                            Accounts.firstName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.lastName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.id,
                            SortOrder.ASC
                        )
                        .toList()
                } else {
                    Accounts
                        .selectAll()
                        .where {
                            (
                                    Accounts.isActive eq
                                            true
                                    ) and (
                                    Accounts.regionId eq
                                            regionScopeId
                                    ) and (
                                    Accounts.categoryId eq
                                            matchingClassId
                                    )
                        }
                        .orderBy(
                            Accounts.firstName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.lastName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.id,
                            SortOrder.ASC
                        )
                        .toList()
                }

            val matchingUsers =
                matchingRows.map { row ->
                    rowToManagerFilteredUser(
                        row
                    )
                }

            println(
                "Direct Manager class query duration: ${
                    System.currentTimeMillis() -
                            classQueryStartedAt
                } ms"
            )

            println(
                "Direct Manager class matching users: ${
                    matchingUsers.size
                }"
            )

            println(
                "Manager repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterRepositoryResult(
                filterType =
                    "class",

                totalCount =
                    matchingUsers.size.toLong(),

                users =
                    matchingUsers
            )
        }
        // here
        val departmentLookupStartedAt =
            System.currentTimeMillis()

        val matchingDepartmentId =
            Departments
                .selectAll()
                .toList()
                .firstOrNull { row ->
                    normalizeValue(
                        row[
                            Departments.departmentName
                        ]
                    ) == normalizedFilter
                }
                ?.get(
                    Departments.id
                )

        println(
            "Manager department lookup duration: ${
                System.currentTimeMillis() -
                        departmentLookupStartedAt
            } ms"
        )

        if (matchingDepartmentId != null) {
            val departmentQueryStartedAt =
                System.currentTimeMillis()

            val matchingRows =
                if (regionScopeId == null) {
                    Accounts
                        .selectAll()
                        .where {
                            (
                                    Accounts.isActive eq
                                            true
                                    ) and (
                                    Accounts.directorateId eq
                                            matchingDepartmentId
                                    )
                        }
                        .orderBy(
                            Accounts.firstName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.lastName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.id,
                            SortOrder.ASC
                        )
                        .toList()
                } else {
                    Accounts
                        .selectAll()
                        .where {
                            (
                                    Accounts.isActive eq
                                            true
                                    ) and (
                                    Accounts.regionId eq
                                            regionScopeId
                                    ) and (
                                    Accounts.directorateId eq
                                            matchingDepartmentId
                                    )
                        }
                        .orderBy(
                            Accounts.firstName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.lastName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.id,
                            SortOrder.ASC
                        )
                        .toList()
                }

            val matchingUsers =
                matchingRows.map { row ->
                    rowToManagerFilteredUser(
                        row
                    )
                }

            val totalCount =
                matchingUsers.size.toLong()

            println(
                "Direct Manager department query duration: ${
                    System.currentTimeMillis() -
                            departmentQueryStartedAt
                } ms"
            )

            println(
                "Direct Manager department matching users: $totalCount"
            )

            println(
                "Manager repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterRepositoryResult(
                filterType =
                    "department",

                totalCount =
                    totalCount,

                users =
                    matchingUsers
            )
        }
        // here
        val managementUnitLookupStartedAt =
            System.currentTimeMillis()

        val matchingManagementUnitId =
            ManagementUnits
                .selectAll()
                .toList()
                .firstOrNull { row ->
                    normalizeValue(
                        row[
                            ManagementUnits.managementUnitName
                        ]
                    ) == normalizedFilter
                }
                ?.get(
                    ManagementUnits.id
                )

        println(
            "Manager management-unit lookup duration: ${
                System.currentTimeMillis() -
                        managementUnitLookupStartedAt
            } ms"
        )

        if (matchingManagementUnitId != null) {
            val managementUnitQueryStartedAt =
                System.currentTimeMillis()

            val matchingRows =
                if (regionScopeId == null) {
                    Accounts
                        .selectAll()
                        .where {
                            (
                                    Accounts.isActive eq
                                            true
                                    ) and (
                                    Accounts.managementUnitCostCentreId eq
                                            matchingManagementUnitId
                                    )
                        }
                        .orderBy(
                            Accounts.firstName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.lastName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.id,
                            SortOrder.ASC
                        )
                        .toList()
                } else {
                    Accounts
                        .selectAll()
                        .where {
                            (
                                    Accounts.isActive eq
                                            true
                                    ) and (
                                    Accounts.regionId eq
                                            regionScopeId
                                    ) and (
                                    Accounts.managementUnitCostCentreId eq
                                            matchingManagementUnitId
                                    )
                        }
                        .orderBy(
                            Accounts.firstName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.lastName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.id,
                            SortOrder.ASC
                        )
                        .toList()
                }

            val matchingUsers =
                matchingRows.map { row ->
                    rowToManagerFilteredUser(
                        row
                    )
                }

            val totalCount =
                matchingUsers.size.toLong()

            println(
                "Direct Manager management-unit query duration: ${
                    System.currentTimeMillis() -
                            managementUnitQueryStartedAt
                } ms"
            )

            println(
                "Direct Manager management-unit matching users: $totalCount"
            )

            println(
                "Manager repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterRepositoryResult(
                filterType =
                    "unit",

                totalCount =
                    totalCount,

                users =
                    matchingUsers
            )
        }
        // HERE
        val leaveTypeLookupStartedAt =
            System.currentTimeMillis()

        val matchingLeaveTypeId =
            OnLeaveTypes
                .selectAll()
                .toList()
                .firstOrNull { row ->
                    normalizeValue(
                        row[
                            OnLeaveTypes.name
                        ]
                    ) == normalizedFilter
                }
                ?.get(
                    OnLeaveTypes.id
                )

        println(
            "Manager leave-type lookup duration: ${
                System.currentTimeMillis() -
                        leaveTypeLookupStartedAt
            } ms"
        )

        if (matchingLeaveTypeId != null) {
            val leaveTypeQueryStartedAt =
                System.currentTimeMillis()

            val matchingRows =
                if (regionScopeId == null) {
                    Accounts
                        .selectAll()
                        .where {
                            (
                                    Accounts.isActive eq
                                            true
                                    ) and (
                                    Accounts.onLeaveTypeId eq
                                            matchingLeaveTypeId
                                    )
                        }
                        .orderBy(
                            Accounts.firstName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.lastName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.id,
                            SortOrder.ASC
                        )
                        .toList()
                } else {
                    Accounts
                        .selectAll()
                        .where {
                            (
                                    Accounts.isActive eq
                                            true
                                    ) and (
                                    Accounts.regionId eq
                                            regionScopeId
                                    ) and (
                                    Accounts.onLeaveTypeId eq
                                            matchingLeaveTypeId
                                    )
                        }
                        .orderBy(
                            Accounts.firstName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.lastName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.id,
                            SortOrder.ASC
                        )
                        .toList()
                }

            val matchingUsers =
                matchingRows.map { row ->
                    rowToManagerFilteredUser(
                        row
                    )
                }

            val totalCount =
                matchingUsers.size.toLong()

            println(
                "Direct Manager leave-type query duration: ${
                    System.currentTimeMillis() -
                            leaveTypeQueryStartedAt
                } ms"
            )

            println(
                "Direct Manager leave-type matching users: $totalCount"
            )

            println(
                "Manager repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterRepositoryResult(
                filterType =
                    "leave type",

                totalCount =
                    totalCount,

                users =
                    matchingUsers
            )
        }
        // HERE

        val matchingStaffCategory =
            when (normalizedFilter) {
                "SENIOR STAFF" -> {
                    StaffCategory.SENIOR_STAFF
                }

                "JUNIOR STAFF" -> {
                    StaffCategory.JUNIOR_STAFF
                }

                else -> {
                    null
                }
            }

        if (matchingStaffCategory != null) {
            val staffCategoryQueryStartedAt =
                System.currentTimeMillis()

            val matchingRows =
                if (regionScopeId == null) {
                    Accounts
                        .selectAll()
                        .where {
                            (
                                    Accounts.isActive eq
                                            true
                                    ) and (
                                    Accounts.staffCategory eq
                                            matchingStaffCategory
                                    )
                        }
                        .orderBy(
                            Accounts.firstName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.lastName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.id,
                            SortOrder.ASC
                        )
                        .toList()
                } else {
                    Accounts
                        .selectAll()
                        .where {
                            (
                                    Accounts.isActive eq
                                            true
                                    ) and (
                                    Accounts.regionId eq
                                            regionScopeId
                                    ) and (
                                    Accounts.staffCategory eq
                                            matchingStaffCategory
                                    )
                        }
                        .orderBy(
                            Accounts.firstName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.lastName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.id,
                            SortOrder.ASC
                        )
                        .toList()
                }

            val matchingUsers =
                matchingRows.map { row ->
                    rowToManagerFilteredUser(
                        row
                    )
                }

            val totalCount =
                matchingUsers.size.toLong()

            println(
                "Direct Manager staff-category query duration: ${
                    System.currentTimeMillis() -
                            staffCategoryQueryStartedAt
                } ms"
            )

            println(
                "Direct Manager staff-category matching users: $totalCount"
            )

            println(
                "Manager repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterRepositoryResult(
                filterType =
                    "staff category",

                totalCount =
                    totalCount,

                users =
                    matchingUsers
            )
        }
        // HERE
        val matchingGender =
            when (normalizedFilter) {
                "MALE" -> {
                    Gender.Male
                }

                "FEMALE" -> {
                    Gender.Female
                }

                else -> {
                    null
                }
            }

        if (matchingGender != null) {
            val genderQueryStartedAt =
                System.currentTimeMillis()

            val matchingRows =
                if (regionScopeId == null) {
                    Accounts
                        .selectAll()
                        .where {
                            (
                                    Accounts.isActive eq
                                            true
                                    ) and (
                                    Accounts.gender eq
                                            matchingGender
                                    )
                        }
                        .orderBy(
                            Accounts.firstName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.lastName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.id,
                            SortOrder.ASC
                        )
                        .toList()
                } else {
                    Accounts
                        .selectAll()
                        .where {
                            (
                                    Accounts.isActive eq
                                            true
                                    ) and (
                                    Accounts.regionId eq
                                            regionScopeId
                                    ) and (
                                    Accounts.gender eq
                                            matchingGender
                                    )
                        }
                        .orderBy(
                            Accounts.firstName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.lastName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.id,
                            SortOrder.ASC
                        )
                        .toList()
                }

            val matchingUsers =
                matchingRows.map { row ->
                    rowToManagerFilteredUser(
                        row
                    )
                }

            val totalCount =
                matchingUsers.size.toLong()

            println(
                "Direct Manager gender query duration: ${
                    System.currentTimeMillis() -
                            genderQueryStartedAt
                } ms"
            )

            println(
                "Direct Manager gender matching users: $totalCount"
            )

            println(
                "Manager repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterRepositoryResult(
                filterType =
                    "gender category",

                totalCount =
                    totalCount,

                users =
                    matchingUsers
            )
        }
        // HERE
        val matchingContractType =
            when (normalizedFilter) {
                "FULLTIME",
                "FULL TIME" -> {
                    FulltimeContractStaff.FULLTIME
                }

                "CONTRACT" -> {
                    FulltimeContractStaff.CONTRACT
                }

                else -> {
                    null
                }
            }

        if (matchingContractType != null) {
            val contractTypeQueryStartedAt =
                System.currentTimeMillis()

            val matchingRows =
                if (regionScopeId == null) {
                    Accounts
                        .selectAll()
                        .where {
                            (
                                    Accounts.isActive eq
                                            true
                                    ) and (
                                    Accounts.fulltimeContractStaff eq
                                            matchingContractType
                                    )
                        }
                        .orderBy(
                            Accounts.firstName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.lastName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.id,
                            SortOrder.ASC
                        )
                        .toList()
                } else {
                    Accounts
                        .selectAll()
                        .where {
                            (
                                    Accounts.isActive eq
                                            true
                                    ) and (
                                    Accounts.regionId eq
                                            regionScopeId
                                    ) and (
                                    Accounts.fulltimeContractStaff eq
                                            matchingContractType
                                    )
                        }
                        .orderBy(
                            Accounts.firstName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.lastName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.id,
                            SortOrder.ASC
                        )
                        .toList()
                }

            val matchingUsers =
                matchingRows.map { row ->
                    rowToManagerFilteredUser(
                        row
                    )
                }

            val totalCount =
                matchingUsers.size.toLong()

            println(
                "Direct Manager contract-type query duration: ${
                    System.currentTimeMillis() -
                            contractTypeQueryStartedAt
                } ms"
            )

            println(
                "Direct Manager contract-type matching users: $totalCount"
            )

            println(
                "Manager repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterRepositoryResult(
                filterType =
                    "agreement type",

                totalCount =
                    totalCount,

                users =
                    matchingUsers
            )
        }
        // HERE
        val matchingProfessionalType =
            when (normalizedFilter) {
                "PROFESSIONAL" -> {
                    Professional.Professional
                }

                "SUBPROFESSIONAL",
                "SUB PROFESSIONAL" -> {
                    Professional.Subprofessional
                }

                else -> {
                    null
                }
            }

        if (matchingProfessionalType != null) {
            val professionalQueryStartedAt =
                System.currentTimeMillis()

            val matchingRows =
                if (regionScopeId == null) {
                    Accounts
                        .selectAll()
                        .where {
                            (
                                    Accounts.isActive eq
                                            true
                                    ) and (
                                    Accounts.professional eq
                                            matchingProfessionalType
                                    )
                        }
                        .orderBy(
                            Accounts.firstName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.lastName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.id,
                            SortOrder.ASC
                        )
                        .toList()
                } else {
                    Accounts
                        .selectAll()
                        .where {
                            (
                                    Accounts.isActive eq
                                            true
                                    ) and (
                                    Accounts.regionId eq
                                            regionScopeId
                                    ) and (
                                    Accounts.professional eq
                                            matchingProfessionalType
                                    )
                        }
                        .orderBy(
                            Accounts.firstName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.lastName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.id,
                            SortOrder.ASC
                        )
                        .toList()
                }

            val matchingUsers =
                matchingRows.map { row ->
                    rowToManagerFilteredUser(
                        row
                    )
                }

            val totalCount =
                matchingUsers.size.toLong()

            println(
                "Direct Manager professional query duration: ${
                    System.currentTimeMillis() -
                            professionalQueryStartedAt
                } ms"
            )

            println(
                "Direct Manager professional matching users: $totalCount"
            )

            println(
                "Manager repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterRepositoryResult(
                filterType =
                    "professional type",

                totalCount =
                    totalCount,

                users =
                    matchingUsers
            )
        }
        // HERE
        val normalizedSalaryPoint =
            normalizedFilter
                .replace(
                    oldValue = "POINT.",
                    newValue = "POINT_"
                )
                .replace(
                    oldValue = "POINT ",
                    newValue = "POINT_"
                )

        val matchingSalaryPoint =
            SalaryPoint.entries
                .firstOrNull { salaryPoint ->
                    salaryPoint.name ==
                            normalizedSalaryPoint
                }

        if (matchingSalaryPoint != null) {
            val salaryPointQueryStartedAt =
                System.currentTimeMillis()

            val matchingRows =
                if (regionScopeId == null) {
                    Accounts
                        .selectAll()
                        .where {
                            (
                                    Accounts.isActive eq
                                            true
                                    ) and (
                                    Accounts.currentSalaryPoint eq
                                            matchingSalaryPoint
                                    )
                        }
                        .orderBy(
                            Accounts.firstName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.lastName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.id,
                            SortOrder.ASC
                        )
                        .toList()
                } else {
                    Accounts
                        .selectAll()
                        .where {
                            (
                                    Accounts.isActive eq
                                            true
                                    ) and (
                                    Accounts.regionId eq
                                            regionScopeId
                                    ) and (
                                    Accounts.currentSalaryPoint eq
                                            matchingSalaryPoint
                                    )
                        }
                        .orderBy(
                            Accounts.firstName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.lastName,
                            SortOrder.ASC
                        )
                        .orderBy(
                            Accounts.id,
                            SortOrder.ASC
                        )
                        .toList()
                }

            val matchingUsers =
                matchingRows.map { row ->
                    rowToManagerFilteredUser(
                        row
                    )
                }

            val totalCount =
                matchingUsers.size.toLong()

            println(
                "Direct Manager salary-point query duration: ${
                    System.currentTimeMillis() -
                            salaryPointQueryStartedAt
                } ms"
            )

            println(
                "Requested salary point: $normalizedSalaryPoint"
            )

            println(
                "Direct Manager salary-point matching users: $totalCount"
            )

            println(
                "Manager repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterRepositoryResult(
                filterType =
                    "salary point",

                totalCount =
                    totalCount,

                users =
                    matchingUsers
            )
        }
        // HERE
        val requestedAgeRange =
            when (normalizedFilter) {
                "20 30",
                "20 - 30",
                "20-30" -> {
                    20 to 30
                }

                "31 40",
                "31 - 40",
                "31-40" -> {
                    31 to 40
                }

                "41 50",
                "41 - 50",
                "41-50" -> {
                    41 to 50
                }

                "51 60",
                "51 - 60",
                "51-60" -> {
                    51 to 60
                }

                else -> {
                    null
                }
            }

        val isSixtyOneAndAbove =
            normalizedFilter == "61+"

        if (
            requestedAgeRange != null ||
            isSixtyOneAndAbove
        ) {
            val ageQueryStartedAt =
                System.currentTimeMillis()

            val today =
                java.time.LocalDate.now()

            val minimumDateOfBirth:
                    java.time.LocalDate?

            val maximumDateOfBirth:
                    java.time.LocalDate

            if (isSixtyOneAndAbove) {
                minimumDateOfBirth =
                    null

                maximumDateOfBirth =
                    today.minusYears(
                        61L
                    )
            } else {
                val minimumAge =
                    requestedAgeRange!!.first

                val maximumAge =
                    requestedAgeRange.second

                minimumDateOfBirth =
                    today
                        .minusYears(
                            maximumAge.toLong() +
                                    1L
                        )
                        .plusDays(
                            1L
                        )

                maximumDateOfBirth =
                    today.minusYears(
                        minimumAge.toLong()
                    )
            }

            val matchingRows =
                if (isSixtyOneAndAbove) {
                    if (regionScopeId == null) {
                        Accounts
                            .selectAll()
                            .where {
                                (
                                        Accounts.isActive eq
                                                true
                                        ) and (
                                        Accounts.dateOfBirth lessEq
                                                maximumDateOfBirth
                                        )
                            }
                            .orderBy(
                                Accounts.firstName,
                                SortOrder.ASC
                            )
                            .orderBy(
                                Accounts.lastName,
                                SortOrder.ASC
                            )
                            .orderBy(
                                Accounts.id,
                                SortOrder.ASC
                            )
                            .toList()
                    } else {
                        Accounts
                            .selectAll()
                            .where {
                                (
                                        Accounts.isActive eq
                                                true
                                        ) and (
                                        Accounts.regionId eq
                                                regionScopeId
                                        ) and (
                                        Accounts.dateOfBirth lessEq
                                                maximumDateOfBirth
                                        )
                            }
                            .orderBy(
                                Accounts.firstName,
                                SortOrder.ASC
                            )
                            .orderBy(
                                Accounts.lastName,
                                SortOrder.ASC
                            )
                            .orderBy(
                                Accounts.id,
                                SortOrder.ASC
                            )
                            .toList()
                    }
                } else {
                    if (regionScopeId == null) {
                        Accounts
                            .selectAll()
                            .where {
                                (
                                        Accounts.isActive eq
                                                true
                                        ) and (
                                        Accounts.dateOfBirth greaterEq
                                                minimumDateOfBirth!!
                                        ) and (
                                        Accounts.dateOfBirth lessEq
                                                maximumDateOfBirth
                                        )
                            }
                            .orderBy(
                                Accounts.firstName,
                                SortOrder.ASC
                            )
                            .orderBy(
                                Accounts.lastName,
                                SortOrder.ASC
                            )
                            .orderBy(
                                Accounts.id,
                                SortOrder.ASC
                            )
                            .toList()
                    } else {
                        Accounts
                            .selectAll()
                            .where {
                                (
                                        Accounts.isActive eq
                                                true
                                        ) and (
                                        Accounts.regionId eq
                                                regionScopeId
                                        ) and (
                                        Accounts.dateOfBirth greaterEq
                                                minimumDateOfBirth!!
                                        ) and (
                                        Accounts.dateOfBirth lessEq
                                                maximumDateOfBirth
                                        )
                            }
                            .orderBy(
                                Accounts.firstName,
                                SortOrder.ASC
                            )
                            .orderBy(
                                Accounts.lastName,
                                SortOrder.ASC
                            )
                            .orderBy(
                                Accounts.id,
                                SortOrder.ASC
                            )
                            .toList()
                    }
                }

            val matchingUsers =
                matchingRows.map { row ->
                    rowToManagerFilteredUser(
                        row
                    )
                }

            val totalCount =
                matchingUsers.size.toLong()

            println(
                "Direct Manager age-range query duration: ${
                    System.currentTimeMillis() -
                            ageQueryStartedAt
                } ms"
            )

            println(
                "Manager age range: $requestedFilter"
            )

            println(
                "Direct Manager age-range matching users: $totalCount"
            )

            println(
                "Manager repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterRepositoryResult(
                filterType =
                    "age range",

                totalCount =
                    totalCount,

                users =
                    matchingUsers
            )
        }











































        /*
         * Temporary fallback for filter types that have not
         * yet been converted to direct database filtering.
         */
        val accountRows =
            if (regionScopeId == null) {
                Accounts
                    .selectAll()
                    .where {
                        Accounts.isActive eq
                                true
                    }
                    .orderBy(
                        Accounts.firstName,
                        SortOrder.ASC
                    )
                    .orderBy(
                        Accounts.lastName,
                        SortOrder.ASC
                    )
                    .orderBy(
                        Accounts.id,
                        SortOrder.ASC
                    )
                    .toList()
            } else {
                Accounts
                    .selectAll()
                    .where {
                        (
                                Accounts.isActive eq
                                        true
                                ) and (
                                Accounts.regionId eq
                                        regionScopeId
                                )
                    }
                    .orderBy(
                        Accounts.firstName,
                        SortOrder.ASC
                    )
                    .orderBy(
                        Accounts.lastName,
                        SortOrder.ASC
                    )
                    .orderBy(
                        Accounts.id,
                        SortOrder.ASC
                    )
                    .toList()
            }

        println(
            "Active accounts found in region scope: ${
                accountRows.size
            }"
        )

        val regionalAccounts =
            mutableListOf<AccountResponse>()

        for (row in accountRows) {
            val account =
                AccountRepository
                    .rowToAccountResponse(
                        row
                    )

            regionalAccounts.add(
                account
            )
        }

        println(
            "Regional accounts mapped: ${
                regionalAccounts.size
            }"
        )

        val filterType =
            determineFilterType(
                accounts =
                    regionalAccounts,

                requestedFilter =
                    normalizedFilter
            )

        println(
            "Detected filter type: ${
                filterType ?: "None"
            }"
        )

        if (filterType == null) {
            println(
                "The requested filter did not match a supported category"
            )

            println(
                "Manager repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterRepositoryResult(
                filterType =
                    null,

                totalCount =
                    0L,

                users =
                    emptyList()
            )
        }

        val filteredAccounts =
            regionalAccounts.filter { account ->
                accountMatchesFilter(
                    account =
                        account,

                    requestedFilter =
                        normalizedFilter,

                    filterType =
                        filterType,

                    regionScopeId =
                        regionScopeId
                )
            }

        val filteredUsers =
            filteredAccounts.map { account ->
                ManagerFilteredUserResponse(
                    id =
                        account.id,

                    userId =
                        account.userId,

                    firstName =
                        account.firstName,

                    fullName =
                        account.fullName,

                    phoneNumber =
                        account.phoneNumber,

                    profilePictureUrl =
                        account.profilePictureUrl
                )
            }

        val totalCount =
            filteredUsers.size.toLong()

        println(
            "Filtered active accounts: $totalCount"
        )

        println(
            "Manager repository total duration: ${
                System.currentTimeMillis() -
                        repositoryStartedAt
            } ms"
        )

        println(
            "=================================================="
        )

        return ManagerUsersByFilterRepositoryResult(
            filterType =
                filterType,

            totalCount =
                totalCount,

            users =
                filteredUsers
        )
    }
    // here



    private fun rowToManagerFilteredUser(
        row: ResultRow
    ): ManagerFilteredUserResponse {
        val firstName =
            row[Accounts.firstName]
                ?.trim()
                ?.takeIf { value ->
                    value.isNotBlank()
                }

        val middleName =
            row[Accounts.middleName]
                ?.trim()
                ?.takeIf { value ->
                    value.isNotBlank()
                }

        val lastName =
            row[Accounts.lastName]
                ?.trim()
                ?.takeIf { value ->
                    value.isNotBlank()
                }

        val fullName =
            listOfNotNull(
                firstName,
                middleName,
                lastName
            )
                .joinToString(
                    separator = " "
                )
                .trim()

        return ManagerFilteredUserResponse(
            id =
                row[Accounts.id].value,

            userId =
                row[Accounts.userId],

            firstName =
                firstName,

            fullName =
                fullName,

            phoneNumber =
                row[Accounts.phoneNumber],

            profilePictureUrl =
                row[Accounts.profilePictureUrl]
        )
    }



















    private fun determineFilterType(
        accounts: List<AccountResponse>,
        requestedFilter: String
    ): String? {
        println(
            "Determining filter type"
        )

        println(
            "Requested filter: $requestedFilter"
        )

        val normalizedRequestedFilter =
            normalizeValue(
                requestedFilter
            )

        println(
            "Normalized requested filter: $normalizedRequestedFilter"
        )

        val departmentMatched =
            accounts.any { account ->
                valuesMatch(
                    actualValue =
                        account.directorateName,

                    requestedValue =
                        normalizedRequestedFilter
                )
            }

        if (departmentMatched) {
            println(
                "Requested filter matched department"
            )

            return "department"
        }

        val classMatched =
            accounts.any { account ->
                valuesMatch(
                    actualValue =
                        account.categoryName,

                    requestedValue =
                        normalizedRequestedFilter
                )
            }

        if (classMatched) {
            println(
                "Requested filter matched class"
            )

            return "class"
        }

        val regionMatched =
            accounts.any { account ->
                valuesMatch(
                    actualValue =
                        account.regionName,

                    requestedValue =
                        normalizedRequestedFilter
                )
            }

        if (regionMatched) {
            println(
                "Requested filter matched region"
            )

            return "region"
        }

        val managementUnitMatched =
            accounts.any { account ->
                valuesMatch(
                    actualValue =
                        account.managementUnitCostCentreName,

                    requestedValue =
                        normalizedRequestedFilter
                )
            }

        if (managementUnitMatched) {
            println(
                "Requested filter matched management unit"
            )

            return "unit"
        }

        val staffCategoryMatched =
            accounts.any { account ->
                valuesMatch(
                    actualValue =
                        account.staffCategory,

                    requestedValue =
                        normalizedRequestedFilter
                )
            }

        if (staffCategoryMatched) {
            println(
                "Requested filter matched staff category"
            )

            return "staff category"
        }

        val genderMatched =
            accounts.any { account ->
                valuesMatch(
                    actualValue =
                        account.gender,

                    requestedValue =
                        normalizedRequestedFilter
                )
            }

        if (genderMatched) {
            println(
                "Requested filter matched gender"
            )

            return "gender category"
        }

        val leaveTypeMatched =
            accounts.any { account ->
                valuesMatch(
                    actualValue =
                        account.onLeaveTypeName,

                    requestedValue =
                        normalizedRequestedFilter
                )
            }

        if (leaveTypeMatched) {
            println(
                "Requested filter matched leave type"
            )

            return "leave type"
        }

        val agreementTypeMatched =
            accounts.any { account ->
                valuesMatch(
                    actualValue =
                        account.fulltimeContractStaff,

                    requestedValue =
                        normalizedRequestedFilter
                )
            }

        if (agreementTypeMatched) {
            println(
                "Requested filter matched agreement type"
            )

            return "agreement type"
        }

        val professionalTypeMatched =
            accounts.any { account ->
                professionalValuesMatch(
                    actualValue =
                        account.professional,

                    requestedValue =
                        normalizedRequestedFilter
                )
            }

        if (professionalTypeMatched) {
            println(
                "Requested filter matched professional type"
            )

            return "professional type"
        }

        val salaryRangeMatched =
            accounts.any { account ->
                salaryValuesMatch(
                    actualValue =
                        account.currentSalaryLevel,

                    requestedValue =
                        normalizedRequestedFilter
                )
            }

        if (salaryRangeMatched) {
            println(
                "Requested filter matched salary range"
            )

            return "salary range"
        }

        if (
            normalizedRequestedFilter in
            ageRanges.keys ||
            normalizedRequestedFilter ==
            "61+"
        ) {
            println(
                "Requested filter matched age range"
            )

            return "age range"
        }

        println(
            "Requested filter did not match any supported filter type"
        )

        printAvailableFilterValues(
            accounts =
                accounts
        )

        return null
    }


    private fun accountMatchesFilter(
        account: AccountResponse,
        requestedFilter: String,
        filterType: String,
        regionScopeId: Int?
    ): Boolean {
        /*
         * Keep this additional region validation even though
         * the records were already filtered by region.
         */
        if (
            account.regionId !=
            regionScopeId
        ) {
            return false
        }

        if (!account.isActive) {
            return false
        }

        return when (filterType) {
            "department" -> {
                valuesMatch(
                    account.directorateName,
                    requestedFilter
                )
            }

            "class" -> {
                valuesMatch(
                    account.categoryName,
                    requestedFilter
                )
            }

            "region" -> {
                valuesMatch(
                    account.regionName,
                    requestedFilter
                )
            }

            "unit" -> {
                valuesMatch(
                    account.managementUnitCostCentreName,
                    requestedFilter
                )
            }

            "staff category" -> {
                valuesMatch(
                    account.staffCategory,
                    requestedFilter
                )
            }

            "gender category" -> {
                valuesMatch(
                    account.gender,
                    requestedFilter
                )
            }

            "leave type" -> {
                valuesMatch(
                    account.onLeaveTypeName,
                    requestedFilter
                )
            }

            "agreement type" -> {
                valuesMatch(
                    account.fulltimeContractStaff,
                    requestedFilter
                )
            }

            "professional type" -> {
                valuesMatch(
                    account.professional,
                    requestedFilter
                )
            }

            "salary range" -> {
                salaryValuesMatch(
                    account.currentSalaryLevel,
                    requestedFilter
                )
            }

            "age range" -> {
                ageMatches(
                    age =
                        account.age,

                    requestedFilter =
                        requestedFilter
                )
            }

            else -> {
                false
            }
        }
    }

    private fun valuesMatch(
        actualValue: String?,
        requestedValue: String
    ): Boolean {
        val normalizedActualValue =
            normalizeValue(
                actualValue
            )

        val normalizedRequestedValue =
            normalizeValue(
                requestedValue
            )

        val matched =
            normalizedActualValue.isNotBlank() &&
                    normalizedActualValue ==
                    normalizedRequestedValue

        if (matched) {
            println(
                "Filter value matched"
            )

            println(
                "Actual value: $actualValue"
            )

            println(
                "Requested value: $requestedValue"
            )

            println(
                "Normalized value: $normalizedActualValue"
            )
        }

        return matched
    }

    private fun salaryValuesMatch(
        actualValue: String?,
        requestedValue: String
    ): Boolean {
        val normalizedActual =
            normalizeSalaryLevel(
                actualValue
            )

        val normalizedRequested =
            normalizeSalaryLevel(
                requestedValue
            )

        return (
                normalizedActual.isNotBlank() &&
                        normalizedActual ==
                        normalizedRequested
                )
    }

    private fun normalizeSalaryLevel(
        value: String?
    ): String {
        return normalizeValue(
            value
        )
            .replace(
                oldValue =
                    "SS_",

                newValue =
                    "SS."
            )
            .replace(
                oldValue =
                    "SS ",

                newValue =
                    "SS."
            )
    }

    private fun isSalaryRange(
        requestedFilter: String
    ): Boolean {
        val normalizedSalary =
            normalizeSalaryLevel(
                requestedFilter
            )

        val levelNumber =
            normalizedSalary
                .substringAfter(
                    delimiter =
                        "SS.",

                    missingDelimiterValue =
                        ""
                )
                .toIntOrNull()
                ?: return false

        return levelNumber in
                5..25
    }

    private fun ageMatches(
        age: Int?,
        requestedFilter: String
    ): Boolean {
        if (age == null) {
            return false
        }

        if (
            requestedFilter ==
            "61+"
        ) {
            return age >= 61
        }

        val range =
            ageRanges[
                requestedFilter
            ]
                ?: return false

        return age in range
    }

    private fun normalizeValue(
        value: String?
    ): String {
        return value
            ?.trim()
            ?.uppercase(
                Locale.ROOT
            )
            ?.replace(
                oldChar =
                    '_',

                newChar =
                    ' '
            )
            ?.replace(
                oldChar =
                    '-',

                newChar =
                    ' '
            )
            ?.replace(
                oldValue =
                    "&",

                newValue =
                    "AND"
            )
            ?.replace(
                oldValue =
                    "CHIEFTANCY",

                newValue =
                    "CHIEFTAINCY"
            )
            ?.replace(
                regex =
                    Regex(
                        "\\s+"
                    ),

                replacement =
                    " "
            )
            ?.trim()
            ?: ""
    }





    private fun printAvailableFilterValues(
        accounts: List<AccountResponse>
    ) {
        println(
            "Available department values:"
        )

        accounts
            .mapNotNull {
                it.directorateName
            }
            .filter {
                it.isNotBlank()
            }
            .distinct()
            .sorted()
            .forEach { value ->
                println(
                    "Department: $value"
                )
            }

        println(
            "Available class values:"
        )

        accounts
            .mapNotNull {
                it.categoryName
            }
            .filter {
                it.isNotBlank()
            }
            .distinct()
            .sorted()
            .forEach { value ->
                println(
                    "Class: $value"
                )
            }

        println(
            "Available region values:"
        )

        accounts
            .mapNotNull {
                it.regionName
            }
            .filter {
                it.isNotBlank()
            }
            .distinct()
            .sorted()
            .forEach { value ->
                println(
                    "Region: $value"
                )
            }

        println(
            "Available management-unit values:"
        )

        accounts
            .mapNotNull {
                it.managementUnitCostCentreName
            }
            .filter {
                it.isNotBlank()
            }
            .distinct()
            .sorted()
            .forEach { value ->
                println(
                    "Management unit: $value"
                )
            }

        println(
            "Available staff-category values:"
        )

        accounts
            .mapNotNull {
                it.staffCategory
            }
            .filter {
                it.isNotBlank()
            }
            .distinct()
            .sorted()
            .forEach { value ->
                println(
                    "Staff category: $value"
                )
            }
    }
}