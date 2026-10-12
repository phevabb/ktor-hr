package com.hr.manager.repositories

import com.hr.account.dtos.AccountResponse
import com.hr.account.helpers.AccountComputedValues
import com.hr.account.repositories.AccountRepository
import com.hr.account.table.Accounts
import com.hr.manager.dtos.AdminFilteredUserResponse
import java.util.Locale
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
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

        return FilterableAccount(
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
        val normalizedFilter =
            normalizeValue(
                requestedFilter
            )

        if (normalizedFilter.isBlank()) {
            return AdminUsersByFilterRepositoryResult(
                filterType = null,
                totalCount = 0L,
                users = emptyList()
            )
        }

        val safePage =
            page.coerceAtLeast(1)

        val safePageSize =
            pageSize.coerceIn(
                minimumValue = 1,
                maximumValue = 100
            )

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

        for (row in rows) {
            allAccounts.add(
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
            )
        }

        val filterType =
            determineFilterType(
                accounts =
                    allAccounts,

                requestedFilter =
                    normalizedFilter
            )

        if (filterType == null) {
            return AdminUsersByFilterRepositoryResult(
                filterType = null,
                totalCount = 0L,
                users = emptyList()
            )
        }

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

        val pageUsers =
            pageAccounts.map { account ->
                toFilteredUserResponse(
                    account
                )
            }

        return AdminUsersByFilterRepositoryResult(
            filterType = filterType,
            totalCount = totalCount,
            users = pageUsers
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



