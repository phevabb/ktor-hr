package com.hr.manager.repositories

import com.hr.account.dtos.AccountResponse
import com.hr.account.dtos.FulltimeContractStaff
import com.hr.account.dtos.Gender
import com.hr.account.dtos.Professional
import com.hr.account.dtos.SalaryLevel
import com.hr.account.dtos.StaffCategory
import com.hr.account.helpers.AccountComputedValues
import com.hr.account.repositories.AccountRepository
import com.hr.account.table.Accounts
import com.hr.classes.tables.Classes
import com.hr.department.table.Departments
import com.hr.managementUnits.tables.ManagementUnits
import com.hr.manager.dtos.AdminFilteredUserResponse
import com.hr.onleavetype.table.OnLeaveTypes
import com.hr.region.tables.Regions
import java.util.Locale
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll



object AdminUsersByFilterRepository {

    private suspend fun rowToFilterableAccount(
        row: ResultRow,
        directorateNames: MutableMap<Int, String?>,
        categoryNames: MutableMap<Int, String?>,
        districtNames: MutableMap<Int, String?>,
        regionNames: MutableMap<Int, String?>,
        managementUnitNames: MutableMap<Int, String?>,
        leaveTypeNames: MutableMap<Int, String?>
    ): FilterableAccount {
        val accountId =
            row[Accounts.id].value

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
                .takeIf { value ->
                    value.isNotBlank()
                }

        val dateOfBirth =
            row[Accounts.dateOfBirth]

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

        val managementUnitId =
            row[
                Accounts.managementUnitCostCentreId
            ]?.value

        val onLeaveTypeId =
            row[Accounts.onLeaveTypeId]
                ?.value

        val directorateLookupStartedAt =
            System.currentTimeMillis()

        val directorateName =
            if (directorateId == null) {
                null
            } else {
                directorateNames.getOrPut(
                    directorateId
                ) {
                    AccountRepository
                        .findDirectorateName(
                            directorateId
                        )
                }
            }

        val directorateLookupDuration =
            System.currentTimeMillis() -
                    directorateLookupStartedAt

        val categoryLookupStartedAt =
            System.currentTimeMillis()

        val categoryName =
            if (categoryId == null) {
                null
            } else {
                categoryNames.getOrPut(
                    categoryId
                ) {
                    AccountRepository
                        .findCategoryName(
                            categoryId
                        )
                }
            }

        val categoryLookupDuration =
            System.currentTimeMillis() -
                    categoryLookupStartedAt

        val districtLookupStartedAt =
            System.currentTimeMillis()

        val districtName =
            if (districtId == null) {
                null
            } else {
                districtNames.getOrPut(
                    districtId
                ) {
                    AccountRepository
                        .findDistrictName(
                            districtId
                        )
                }
            }

        val districtLookupDuration =
            System.currentTimeMillis() -
                    districtLookupStartedAt

        val regionLookupStartedAt =
            System.currentTimeMillis()

        val regionName =
            if (regionId == null) {
                null
            } else {
                regionNames.getOrPut(
                    regionId
                ) {
                    AccountRepository
                        .findRegionName(
                            regionId
                        )
                }
            }

        val regionLookupDuration =
            System.currentTimeMillis() -
                    regionLookupStartedAt

        val managementUnitLookupStartedAt =
            System.currentTimeMillis()

        val managementUnitName =
            if (managementUnitId == null) {
                null
            } else {
                managementUnitNames.getOrPut(
                    managementUnitId
                ) {
                    AccountRepository
                        .findManagementUnitName(
                            managementUnitId
                        )
                }
            }

        val managementUnitLookupDuration =
            System.currentTimeMillis() -
                    managementUnitLookupStartedAt

        val leaveTypeLookupStartedAt =
            System.currentTimeMillis()

        val onLeaveTypeName =
            if (onLeaveTypeId == null) {
                null
            } else {
                leaveTypeNames.getOrPut(
                    onLeaveTypeId
                ) {
                    AccountRepository
                        .findOnLeaveTypeName(
                            onLeaveTypeId
                        )
                }
            }

        val leaveTypeLookupDuration =
            System.currentTimeMillis() -
                    leaveTypeLookupStartedAt

        val totalLookupDuration =
            directorateLookupDuration +
                    categoryLookupDuration +
                    districtLookupDuration +
                    regionLookupDuration +
                    managementUnitLookupDuration +
                    leaveTypeLookupDuration

        if (totalLookupDuration >= 100L) {
            println(
                "Slow account lookup: " +
                        "accountId=$accountId, " +
                        "total=${totalLookupDuration}ms, " +
                        "directorate=${directorateLookupDuration}ms, " +
                        "category=${categoryLookupDuration}ms, " +
                        "district=${districtLookupDuration}ms, " +
                        "region=${regionLookupDuration}ms, " +
                        "managementUnit=${managementUnitLookupDuration}ms, " +
                        "leaveType=${leaveTypeLookupDuration}ms"
            )
        }

        return FilterableAccount(
            id =
                accountId,

            userId =
                row[Accounts.userId],

            firstName =
                firstName,

            lastName =
                lastName,

            fullName =
                fullName,

            phoneNumber =
                row[Accounts.phoneNumber],

            profilePictureUrl =
                row[Accounts.profilePictureUrl],

            directorateId =
                directorateId,

            directorateName =
                directorateName,

            categoryId =
                categoryId,

            categoryName =
                categoryName,

            districtId =
                districtId,

            districtName =
                districtName,

            regionId =
                regionId,

            regionName =
                regionName,

            managementUnitId =
                managementUnitId,

            managementUnitCostCentreName =
                managementUnitName,

            onLeaveTypeId =
                onLeaveTypeId,

            onLeaveTypeName =
                onLeaveTypeName,

            staffCategory =
                row[Accounts.staffCategory]
                    ?.name,

            gender =
                row[Accounts.gender]
                    ?.name,

            fulltimeContractStaff =
                row[
                    Accounts.fulltimeContractStaff
                ]?.name,

            professional =
                row[Accounts.professional]
                    ?.name,

            currentSalaryLevel =
                row[
                    Accounts.currentSalaryLevel
                ]?.name,

            age =
                AccountComputedValues.age(
                    dateOfBirth
                ),

            isActive =
                row[Accounts.isActive]
        )
    }


    private fun toFilteredUserResponse(
        account: FilterableAccount
    ): AdminFilteredUserResponse {
        return AdminFilteredUserResponse(
            id =
                account.id,

            userId =
                account.userId,

            firstName =
                account.firstName,

            lastName =
                account.lastName,

            fullName =
                account.fullName,

            phoneNumber =
                account.phoneNumber,

            profilePictureUrl =
                account.profilePictureUrl
        )
    }


    private data class FilterableAccount(
        val id: Int,
        val userId: String?,
        val firstName: String?,
        val lastName: String?,
        val fullName: String?,
        val phoneNumber: String?,
        val profilePictureUrl: String?,

        val directorateId: Int?,
        val directorateName: String?,

        val categoryId: Int?,
        val categoryName: String?,

        val districtId: Int?,
        val districtName: String?,

        val regionId: Int?,
        val regionName: String?,

        val managementUnitId: Int?,
        val managementUnitCostCentreName: String?,

        val onLeaveTypeId: Int?,
        val onLeaveTypeName: String?,

        val staffCategory: String?,
        val gender: String?,
        val fulltimeContractStaff: String?,
        val professional: String?,
        val currentSalaryLevel: String?,
        val age: Int?,
        val isActive: Boolean
    )

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

    suspend fun getUsersByFilter(
        requestedFilter: String,
        page: Int,
        pageSize: Int
    ): AdminUsersByFilterRepositoryResult {
        val repositoryStartedAt =
            System.currentTimeMillis()

        println(
            "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
        )

        println(
            "AdminUsersByFilterRepository started"
        )

        val normalizationStartedAt =
            System.currentTimeMillis()

        val normalizedFilter =
            normalizeValue(
                requestedFilter
            )

        val safePage =
            page.coerceAtLeast(1)

        val safePageSize =
            pageSize.coerceIn(
                minimumValue = 1,
                maximumValue = 100
            )





        // class start //
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
            "Class lookup duration: ${
                System.currentTimeMillis() -
                        classLookupStartedAt
            } ms"
        )

        if (matchingClassId != null) {
            val classQueryStartedAt =
                System.currentTimeMillis()

            val classCondition = {
                (
                        Accounts.isActive eq true
                        ) and (
                        Accounts.categoryId eq
                                matchingClassId
                        )
            }

            val totalCount =
                Accounts
                    .selectAll()
                    .where(
                        classCondition
                    )
                    .count()

            val totalPages =
                if (totalCount == 0L) {
                    1
                } else {
                    (
                            (
                                    totalCount +
                                            safePageSize.toLong() -
                                            1L
                                    ) /
                                    safePageSize.toLong()
                            ).toInt()
                }

            val effectivePage =
                safePage.coerceIn(
                    minimumValue = 1,
                    maximumValue = totalPages
                )

            val offset =
                (
                        effectivePage - 1
                        ).toLong() *
                        safePageSize.toLong()

            val pageRows =
                Accounts
                    .selectAll()
                    .where(
                        classCondition
                    )
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
                    .limit(
                        safePageSize
                    )
                    .offset(
                        offset
                    )
                    .toList()

            val pageUsers =
                pageRows.map { row ->
                    rowToLightweightUser(
                        row
                    )
                }

            println(
                "Direct class query duration: ${
                    System.currentTimeMillis() -
                            classQueryStartedAt
                } ms"
            )

            println(
                "Direct class matching users: $totalCount"
            )

            println(
                "Direct class users returned: ${pageUsers.size}"
            )

            println(
                "Repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
            )

            return AdminUsersByFilterRepositoryResult(
                filterType =
                    "class",

                totalCount =
                    totalCount,

                users =
                    pageUsers
            )
        }
        // here class ends //
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
            "Department lookup duration: ${
                System.currentTimeMillis() -
                        departmentLookupStartedAt
            } ms"
        )

        if (matchingDepartmentId != null) {
            val departmentQueryStartedAt =
                System.currentTimeMillis()

            val totalCount =
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
                    .count()

            val totalPages =
                if (totalCount == 0L) {
                    1
                } else {
                    (
                            (
                                    totalCount +
                                            safePageSize.toLong() -
                                            1L
                                    ) /
                                    safePageSize.toLong()
                            ).toInt()
                }

            val effectivePage =
                safePage.coerceIn(
                    minimumValue = 1,
                    maximumValue = totalPages
                )

            val offset =
                (
                        effectivePage - 1
                        ).toLong() *
                        safePageSize.toLong()

            val pageRows =
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
                    .limit(
                        safePageSize
                    )
                    .offset(
                        offset
                    )
                    .toList()

            val pageUsers =
                pageRows.map { row ->
                    rowToLightweightUser(
                        row
                    )
                }

            println(
                "Direct department query duration: ${
                    System.currentTimeMillis() -
                            departmentQueryStartedAt
                } ms"
            )

            println(
                "Direct department matching users: $totalCount"
            )

            println(
                "Direct department users returned: ${pageUsers.size}"
            )

            println(
                "Repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
            )

            return AdminUsersByFilterRepositoryResult(
                filterType =
                    "department",

                totalCount =
                    totalCount,

                users =
                    pageUsers
            )
        }
        // department ends here//
        val regionLookupStartedAt =
            System.currentTimeMillis()

        val matchingRegionId =
            Regions
                .selectAll()
                .toList()
                .firstOrNull { row ->
                    normalizeValue(
                        row[
                            Regions.region
                        ]
                    ) == normalizedFilter
                }
                ?.get(
                    Regions.id
                )

        println(
            "Region lookup duration: ${
                System.currentTimeMillis() -
                        regionLookupStartedAt
            } ms"
        )

        if (matchingRegionId != null) {
            val regionQueryStartedAt =
                System.currentTimeMillis()

            val totalCount =
                Accounts
                    .selectAll()
                    .where {
                        (
                                Accounts.isActive eq
                                        true
                                ) and (
                                Accounts.regionId eq
                                        matchingRegionId
                                )
                    }
                    .count()

            val totalPages =
                if (totalCount == 0L) {
                    1
                } else {
                    (
                            (
                                    totalCount +
                                            safePageSize.toLong() -
                                            1L
                                    ) /
                                    safePageSize.toLong()
                            ).toInt()
                }

            val effectivePage =
                safePage.coerceIn(
                    minimumValue = 1,
                    maximumValue = totalPages
                )

            val offset =
                (
                        effectivePage - 1
                        ).toLong() *
                        safePageSize.toLong()

            val pageRows =
                Accounts
                    .selectAll()
                    .where {
                        (
                                Accounts.isActive eq
                                        true
                                ) and (
                                Accounts.regionId eq
                                        matchingRegionId
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
                    .limit(
                        safePageSize
                    )
                    .offset(
                        offset
                    )
                    .toList()

            val pageUsers =
                pageRows.map { row ->
                    rowToLightweightUser(
                        row
                    )
                }

            println(
                "Direct region query duration: ${
                    System.currentTimeMillis() -
                            regionQueryStartedAt
                } ms"
            )

            println(
                "Direct region matching users: $totalCount"
            )

            println(
                "Direct region users returned: ${pageUsers.size}"
            )

            println(
                "Repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
            )

            return AdminUsersByFilterRepositoryResult(
                filterType =
                    "region",

                totalCount =
                    totalCount,

                users =
                    pageUsers
            )
        }
        // here //
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
            "Management unit lookup duration: ${
                System.currentTimeMillis() -
                        managementUnitLookupStartedAt
            } ms"
        )

        if (matchingManagementUnitId != null) {
            val managementUnitQueryStartedAt =
                System.currentTimeMillis()

            val totalCount =
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
                    .count()

            val totalPages =
                if (totalCount == 0L) {
                    1
                } else {
                    (
                            (
                                    totalCount +
                                            safePageSize.toLong() -
                                            1L
                                    ) /
                                    safePageSize.toLong()
                            ).toInt()
                }

            val effectivePage =
                safePage.coerceIn(
                    minimumValue = 1,
                    maximumValue = totalPages
                )

            val offset =
                (
                        effectivePage - 1
                        ).toLong() *
                        safePageSize.toLong()

            val pageRows =
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
                    .limit(
                        safePageSize
                    )
                    .offset(
                        offset
                    )
                    .toList()

            val pageUsers =
                pageRows.map { row ->
                    rowToLightweightUser(
                        row
                    )
                }

            println(
                "Direct management unit query duration: ${
                    System.currentTimeMillis() -
                            managementUnitQueryStartedAt
                } ms"
            )

            println(
                "Direct management unit matching users: $totalCount"
            )

            println(
                "Direct management unit users returned: ${pageUsers.size}"
            )

            println(
                "Repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
            )

            return AdminUsersByFilterRepositoryResult(
                filterType =
                    "unit",

                totalCount =
                    totalCount,

                users =
                    pageUsers
            )
        }


        // HERE //
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
            "Leave type lookup duration: ${
                System.currentTimeMillis() -
                        leaveTypeLookupStartedAt
            } ms"
        )

        if (matchingLeaveTypeId != null) {
            val leaveTypeQueryStartedAt =
                System.currentTimeMillis()

            val totalCount =
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
                    .count()

            val totalPages =
                if (totalCount == 0L) {
                    1
                } else {
                    (
                            (
                                    totalCount +
                                            safePageSize.toLong() -
                                            1L
                                    ) /
                                    safePageSize.toLong()
                            ).toInt()
                }

            val effectivePage =
                safePage.coerceIn(
                    minimumValue = 1,
                    maximumValue = totalPages
                )

            val offset =
                (
                        effectivePage - 1
                        ).toLong() *
                        safePageSize.toLong()

            val pageRows =
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
                    .limit(
                        safePageSize
                    )
                    .offset(
                        offset
                    )
                    .toList()

            val pageUsers =
                pageRows.map { row ->
                    rowToLightweightUser(
                        row
                    )
                }

            println(
                "Direct leave type query duration: ${
                    System.currentTimeMillis() -
                            leaveTypeQueryStartedAt
                } ms"
            )

            println(
                "Direct leave type matching users: $totalCount"
            )

            println(
                "Direct leave type users returned: ${pageUsers.size}"
            )

            println(
                "Repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
            )

            return AdminUsersByFilterRepositoryResult(
                filterType =
                    "leave type",

                totalCount =
                    totalCount,

                users =
                    pageUsers
            )
        }
        //HERE
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

            val totalCount =
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
                    .count()

            val totalPages =
                if (totalCount == 0L) {
                    1
                } else {
                    (
                            totalCount +
                                    safePageSize.toLong() -
                                    1L
                            )
                        .div(
                            safePageSize.toLong()
                        )
                        .toInt()
                }

            val effectivePage =
                safePage.coerceIn(
                    minimumValue = 1,
                    maximumValue = totalPages
                )

            val offset =
                (
                        effectivePage - 1
                        ).toLong() *
                        safePageSize.toLong()

            val pageRows =
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
                    .limit(
                        safePageSize
                    )
                    .offset(
                        offset
                    )
                    .toList()

            val pageUsers =
                pageRows.map { row ->
                    rowToLightweightUser(
                        row
                    )
                }

            println(
                "Direct staff category query duration: ${
                    System.currentTimeMillis() -
                            staffCategoryQueryStartedAt
                } ms"
            )

            println(
                "Direct staff category matching users: $totalCount"
            )

            println(
                "Direct staff category users returned: ${pageUsers.size}"
            )

            println(
                "Repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
            )

            return AdminUsersByFilterRepositoryResult(
                filterType =
                    "staff category",

                totalCount =
                    totalCount,

                users =
                    pageUsers
            )
        }
        // here

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

            val totalCount =
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
                    .count()

            val totalPages =
                if (totalCount == 0L) {
                    1
                } else {
                    (
                            totalCount +
                                    safePageSize.toLong() -
                                    1L
                            )
                        .div(
                            safePageSize.toLong()
                        )
                        .toInt()
                }

            val effectivePage =
                safePage.coerceIn(
                    minimumValue = 1,
                    maximumValue = totalPages
                )

            val offset =
                (
                        effectivePage - 1
                        ).toLong() *
                        safePageSize.toLong()

            val pageRows =
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
                    .limit(
                        safePageSize
                    )
                    .offset(
                        offset
                    )
                    .toList()

            val pageUsers =
                pageRows.map { row ->
                    rowToLightweightUser(
                        row
                    )
                }

            println(
                "Direct gender query duration: ${
                    System.currentTimeMillis() -
                            genderQueryStartedAt
                } ms"
            )

            println(
                "Direct gender matching users: $totalCount"
            )

            println(
                "Direct gender users returned: ${pageUsers.size}"
            )

            println(
                "Repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
            )

            return AdminUsersByFilterRepositoryResult(
                filterType =
                    "gender category",

                totalCount =
                    totalCount,

                users =
                    pageUsers
            )
        }
// HERE

        val matchingAgreementType =
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

        if (matchingAgreementType != null) {
            val agreementTypeQueryStartedAt =
                System.currentTimeMillis()

            val totalCount =
                Accounts
                    .selectAll()
                    .where {
                        (
                                Accounts.isActive eq
                                        true
                                ) and (
                                Accounts.fulltimeContractStaff eq
                                        matchingAgreementType
                                )
                    }
                    .count()

            val totalPages =
                if (totalCount == 0L) {
                    1
                } else {
                    (
                            totalCount +
                                    safePageSize.toLong() -
                                    1L
                            )
                        .div(
                            safePageSize.toLong()
                        )
                        .toInt()
                }

            val effectivePage =
                safePage.coerceIn(
                    minimumValue = 1,
                    maximumValue = totalPages
                )

            val offset =
                (
                        effectivePage - 1
                        ).toLong() *
                        safePageSize.toLong()

            val pageRows =
                Accounts
                    .selectAll()
                    .where {
                        (
                                Accounts.isActive eq
                                        true
                                ) and (
                                Accounts.fulltimeContractStaff eq
                                        matchingAgreementType
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
                    .limit(
                        safePageSize
                    )
                    .offset(
                        offset
                    )
                    .toList()

            val pageUsers =
                pageRows.map { row ->
                    rowToLightweightUser(
                        row
                    )
                }

            println(
                "Direct agreement type query duration: ${
                    System.currentTimeMillis() -
                            agreementTypeQueryStartedAt
                } ms"
            )

            println(
                "Direct agreement type matching users: $totalCount"
            )

            println(
                "Direct agreement type users returned: ${pageUsers.size}"
            )

            println(
                "Repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
            )

            return AdminUsersByFilterRepositoryResult(
                filterType =
                    "agreement type",

                totalCount =
                    totalCount,

                users =
                    pageUsers
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
            val professionalTypeQueryStartedAt =
                System.currentTimeMillis()

            val totalCount =
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
                    .count()

            val totalPages =
                if (totalCount == 0L) {
                    1
                } else {
                    (
                            totalCount +
                                    safePageSize.toLong() -
                                    1L
                            )
                        .div(
                            safePageSize.toLong()
                        )
                        .toInt()
                }

            val effectivePage =
                safePage.coerceIn(
                    minimumValue = 1,
                    maximumValue = totalPages
                )

            val offset =
                (
                        effectivePage - 1
                        ).toLong() *
                        safePageSize.toLong()

            val pageRows =
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
                    .limit(
                        safePageSize
                    )
                    .offset(
                        offset
                    )
                    .toList()

            val pageUsers =
                pageRows.map { row ->
                    rowToLightweightUser(
                        row
                    )
                }

            println(
                "Direct professional type query duration: ${
                    System.currentTimeMillis() -
                            professionalTypeQueryStartedAt
                } ms"
            )

            println(
                "Direct professional type matching users: $totalCount"
            )

            println(
                "Direct professional type users returned: ${pageUsers.size}"
            )

            println(
                "Repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
            )

            return AdminUsersByFilterRepositoryResult(
                filterType =
                    "professional type",

                totalCount =
                    totalCount,

                users =
                    pageUsers
            )
        }
        // HERE

        val normalizedSalaryLevel =
            normalizedFilter
                .replace(
                    oldValue = "SS.",
                    newValue = "SS_"
                )
                .replace(
                    oldValue = "SS ",
                    newValue = "SS_"
                )

        val matchingSalaryLevel =
            SalaryLevel.entries
                .firstOrNull { salaryLevel ->
                    salaryLevel.name ==
                            normalizedSalaryLevel
                }

        if (matchingSalaryLevel != null) {
            val salaryLevelQueryStartedAt =
                System.currentTimeMillis()

            val totalCount =
                Accounts
                    .selectAll()
                    .where {
                        (
                                Accounts.isActive eq
                                        true
                                ) and (
                                Accounts.currentSalaryLevel eq
                                        matchingSalaryLevel
                                )
                    }
                    .count()

            val totalPages =
                if (totalCount == 0L) {
                    1
                } else {
                    (
                            totalCount +
                                    safePageSize.toLong() -
                                    1L
                            )
                        .div(
                            safePageSize.toLong()
                        )
                        .toInt()
                }

            val effectivePage =
                safePage.coerceIn(
                    minimumValue = 1,
                    maximumValue = totalPages
                )

            val offset =
                (
                        effectivePage - 1
                        ).toLong() *
                        safePageSize.toLong()

            val pageRows =
                Accounts
                    .selectAll()
                    .where {
                        (
                                Accounts.isActive eq
                                        true
                                ) and (
                                Accounts.currentSalaryLevel eq
                                        matchingSalaryLevel
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
                    .limit(
                        safePageSize
                    )
                    .offset(
                        offset
                    )
                    .toList()

            val pageUsers =
                pageRows.map { row ->
                    rowToLightweightUser(
                        row
                    )
                }

            println(
                "Direct salary level query duration: ${
                    System.currentTimeMillis() -
                            salaryLevelQueryStartedAt
                } ms"
            )

            println(
                "Requested salary level: $normalizedSalaryLevel"
            )

            println(
                "Direct salary level matching users: $totalCount"
            )

            println(
                "Direct salary level users returned: ${pageUsers.size}"
            )

            println(
                "Effective salary level page: $effectivePage"
            )

            println(
                "Salary level total pages: $totalPages"
            )

            println(
                "Repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
            )

            return AdminUsersByFilterRepositoryResult(
                filterType =
                    "salary range",

                totalCount =
                    totalCount,

                users =
                    pageUsers
            )
        }
        // HERE
        val requestedAgeRange =
            when (normalizedFilter) {
                "20 - 30",
                "20-30" -> {
                    20 to 30
                }

                "31 - 40",
                "31-40" -> {
                    31 to 40
                }

                "41 - 50",
                "41-50" -> {
                    41 to 50
                }

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


        // here
























































        println(
            "Repository parameter preparation duration: ${
                System.currentTimeMillis() -
                        normalizationStartedAt
            } ms"
        )

        if (normalizedFilter.isBlank()) {
            println(
                "Repository stopped because filter is blank"
            )

            println(
                "Repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
            )

            return AdminUsersByFilterRepositoryResult(
                filterType = null,
                totalCount = 0L,
                users = emptyList()
            )
        }

        val accountQueryStartedAt =
            System.currentTimeMillis()

        val rows =
            Accounts
                .selectAll()
                .where {
                    Accounts.isActive eq true
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

        println(
            "Active accounts database query duration: ${
                System.currentTimeMillis() -
                        accountQueryStartedAt
            } ms"
        )

        println(
            "Active account rows retrieved: ${rows.size}"
        )

        val cacheCreationStartedAt =
            System.currentTimeMillis()

        val directorateNames =
            mutableMapOf<Int, String?>()

        val categoryNames =
            mutableMapOf<Int, String?>()

        val districtNames =
            mutableMapOf<Int, String?>()

        val regionNames =
            mutableMapOf<Int, String?>()

        val managementUnitNames =
            mutableMapOf<Int, String?>()

        val leaveTypeNames =
            mutableMapOf<Int, String?>()

        val allAccounts =
            ArrayList<FilterableAccount>(
                rows.size
            )

        println(
            "Lookup cache creation duration: ${
                System.currentTimeMillis() -
                        cacheCreationStartedAt
            } ms"
        )

        val accountMappingStartedAt =
            System.currentTimeMillis()

        var slowestAccountDuration =
            0L

        var slowestAccountId =
            0

        for (row in rows) {
            val singleAccountStartedAt =
                System.currentTimeMillis()

            val account =
                rowToFilterableAccount(
                    row = row,

                    directorateNames =
                        directorateNames,

                    categoryNames =
                        categoryNames,

                    districtNames =
                        districtNames,

                    regionNames =
                        regionNames,

                    managementUnitNames =
                        managementUnitNames,

                    leaveTypeNames =
                        leaveTypeNames
                )

            val singleAccountDuration =
                System.currentTimeMillis() -
                        singleAccountStartedAt

            if (
                singleAccountDuration >
                slowestAccountDuration
            ) {
                slowestAccountDuration =
                    singleAccountDuration

                slowestAccountId =
                    account.id
            }

            allAccounts.add(
                account
            )
        }

        println(
            "Filterable accounts mapping duration: ${
                System.currentTimeMillis() -
                        accountMappingStartedAt
            } ms"
        )

        println(
            "Filterable accounts mapped: ${allAccounts.size}"
        )

        println(
            "Slowest account mapping duration: " +
                    "$slowestAccountDuration ms"
        )

        println(
            "Slowest account ID: $slowestAccountId"
        )

        println(
            "Directorate cache entries: ${directorateNames.size}"
        )

        println(
            "Category cache entries: ${categoryNames.size}"
        )

        println(
            "District cache entries: ${districtNames.size}"
        )

        println(
            "Region cache entries: ${regionNames.size}"
        )

        println(
            "Management unit cache entries: ${managementUnitNames.size}"
        )

        println(
            "Leave type cache entries: ${leaveTypeNames.size}"
        )

        val filterTypeStartedAt =
            System.currentTimeMillis()

        val filterType =
            determineFilterType(
                accounts =
                    allAccounts,

                requestedFilter =
                    normalizedFilter
            )

        println(
            "Filter type detection duration: ${
                System.currentTimeMillis() -
                        filterTypeStartedAt
            } ms"
        )

        println(
            "Detected filter type: ${filterType ?: "None"}"
        )

        if (filterType == null) {
            println(
                "Repository total duration: ${
                    System.currentTimeMillis() -
                            repositoryStartedAt
                } ms"
            )

            println(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
            )

            return AdminUsersByFilterRepositoryResult(
                filterType = null,
                totalCount = 0L,
                users = emptyList()
            )
        }

        val filteringStartedAt =
            System.currentTimeMillis()

        val matchingAccounts =
            allAccounts.filter { account ->
                accountMatchesFilter(
                    account = account,

                    requestedFilter =
                        normalizedFilter,

                    filterType =
                        filterType
                )
            }

        val totalCount =
            matchingAccounts.size.toLong()

        println(
            "Account filtering duration: ${
                System.currentTimeMillis() -
                        filteringStartedAt
            } ms"
        )

        println(
            "Matching accounts found: $totalCount"
        )

        val paginationStartedAt =
            System.currentTimeMillis()

        val totalPages =
            if (totalCount == 0L) {
                1
            } else {
                (
                        (
                                totalCount +
                                        safePageSize.toLong() -
                                        1L
                                ) /
                                safePageSize.toLong()
                        ).toInt()
            }

        val effectivePage =
            safePage.coerceIn(
                minimumValue = 1,
                maximumValue = totalPages
            )

        val startIndex =
            (
                    effectivePage - 1
                    ) * safePageSize

        val endIndex =
            (
                    startIndex +
                            safePageSize
                    ).coerceAtMost(
                    matchingAccounts.size
                )

        val pageAccounts =
            if (startIndex < endIndex) {
                matchingAccounts.subList(
                    fromIndex = startIndex,
                    toIndex = endIndex
                )
            } else {
                emptyList()
            }

        println(
            "Repository pagination duration: ${
                System.currentTimeMillis() -
                        paginationStartedAt
            } ms"
        )

        println(
            "Effective page: $effectivePage"
        )

        println(
            "Repository total pages: $totalPages"
        )

        println(
            "Accounts selected for page: ${pageAccounts.size}"
        )

        val responseMappingStartedAt =
            System.currentTimeMillis()

        val pageUsers =
            pageAccounts.map { account ->
                toFilteredUserResponse(
                    account
                )
            }

        println(
            "Lightweight response mapping duration: ${
                System.currentTimeMillis() -
                        responseMappingStartedAt
            } ms"
        )

        println(
            "Repository total duration: ${
                System.currentTimeMillis() -
                        repositoryStartedAt
            } ms"
        )

        println(
            "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
        )

        return AdminUsersByFilterRepositoryResult(
            filterType = filterType,
            totalCount = totalCount,
            users = pageUsers
        )
    }


    private fun rowToLightweightUser(
        row: ResultRow
    ): AdminFilteredUserResponse {
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
                .takeIf { value ->
                    value.isNotBlank()
                }

        return AdminFilteredUserResponse(
            id =
                row[Accounts.id].value,

            userId =
                row[Accounts.userId],

            firstName =
                firstName,

            lastName =
                lastName,

            fullName =
                fullName,

            phoneNumber =
                row[Accounts.phoneNumber],

            profilePictureUrl =
                row[Accounts.profilePictureUrl]
        )
    }



    private fun determineFilterType(
        accounts: List<FilterableAccount>,
        requestedFilter: String
    ): String? {
        if (
            accounts.any { account ->
                valuesMatch(
                    account.directorateName,
                    requestedFilter
                )
            }
        ) {
            return "department"
        }

        if (
            accounts.any { account ->
                valuesMatch(
                    account.categoryName,
                    requestedFilter
                )
            }
        ) {
            return "class"
        }

        if (
            accounts.any { account ->
                valuesMatch(
                    account.regionName,
                    requestedFilter
                )
            }
        ) {
            return "region"
        }

        if (
            accounts.any { account ->
                valuesMatch(
                    account.managementUnitCostCentreName,
                    requestedFilter
                )
            }
        ) {
            return "unit"
        }

        if (
            requestedFilter in
            setOf(
                "SENIOR STAFF",
                "JUNIOR STAFF"
            )
        ) {
            return "staff category"
        }

        if (
            requestedFilter in
            setOf(
                "MALE",
                "FEMALE"
            )
        ) {
            return "gender category"
        }

        if (
            accounts.any { account ->
                valuesMatch(
                    account.onLeaveTypeName,
                    requestedFilter
                )
            }
        ) {
            return "leave type"
        }

        if (
            accounts.any { account ->
                valuesMatch(
                    account.fulltimeContractStaff,
                    requestedFilter
                )
            }
        ) {
            return "agreement type"
        }

        if (
            requestedFilter in
            setOf(
                "PROFESSIONAL",
                "SUBPROFESSIONAL",
                "SUB PROFESSIONAL"
            )
        ) {
            return "professional type"
        }

        if (
            isSalaryRange(
                requestedFilter
            )
        ) {
            return "salary range"
        }

        if (
            requestedFilter in
            ageRanges.keys ||
            requestedFilter ==
            "61+"
        ) {
            return "age range"
        }

        return null
    }

    private fun accountMatchesFilter(
        account: FilterableAccount,
        requestedFilter: String,
        filterType: String
    ): Boolean {
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
                professionalValuesMatch(
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
        return normalizeValue(
            actualValue
        ) ==
                normalizeValue(
                    requestedValue
                )
    }

    private fun professionalValuesMatch(
        actualValue: String?,
        requestedValue: String
    ): Boolean {
        val normalizedActual =
            normalizeValue(
                actualValue
            )
                .replace(
                    oldValue =
                        "SUBPROFESSIONAL",

                    newValue =
                        "SUB PROFESSIONAL"
                )

        val normalizedRequested =
            normalizeValue(
                requestedValue
            )
                .replace(
                    oldValue =
                        "SUBPROFESSIONAL",

                    newValue =
                        "SUB PROFESSIONAL"
                )

        return (
                normalizedActual.isNotBlank() &&
                        normalizedActual ==
                        normalizedRequested
                )
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

        val salaryNumber =
            normalizedSalary
                .substringAfter(
                    delimiter =
                        "SS.",

                    missingDelimiterValue =
                        ""
                )
                .toIntOrNull()
                ?: return false

        return salaryNumber in
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

        return age in
                range
    }

    private fun normalizeValue(
        value: String?
    ): String {
        return value
            ?.trim()
            ?.replace(
                oldChar =
                    '_',

                newChar =
                    ' '
            )
            ?.replace(
                regex =
                    Regex(
                        "\\s+"
                    ),

                replacement =
                    " "
            )
            ?.uppercase(
                Locale.ROOT
            )
            ?: ""
    }
}




