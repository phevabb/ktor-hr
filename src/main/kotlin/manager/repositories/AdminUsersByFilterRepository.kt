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
        row: ResultRow
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
            ]
                ?.value

        val onLeaveTypeId =
            row[Accounts.onLeaveTypeId]
                ?.value

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
                AccountRepository
                    .findDirectorateName(
                        directorateId
                    ),

            categoryId =
                categoryId,

            categoryName =
                AccountRepository
                    .findCategoryName(
                        categoryId
                    ),

            districtId =
                districtId,

            districtName =
                AccountRepository
                    .findDistrictName(
                        districtId
                    ),

            regionId =
                regionId,

            regionName =
                AccountRepository
                    .findRegionName(
                        regionId
                    ),

            managementUnitId =
                managementUnitId,

            managementUnitCostCentreName =
                AccountRepository
                    .findManagementUnitName(
                        managementUnitId
                    ),

            onLeaveTypeId =
                onLeaveTypeId,

            onLeaveTypeName =
                AccountRepository
                    .findOnLeaveTypeName(
                        onLeaveTypeId
                    ),

            staffCategory =
                row[Accounts.staffCategory]
                    ?.name,

            gender =
                row[Accounts.gender]
                    ?.name,

            fulltimeContractStaff =
                row[
                    Accounts.fulltimeContractStaff
                ]
                    ?.name,

            professional =
                row[Accounts.professional]
                    ?.name,

            currentSalaryLevel =
                row[
                    Accounts.currentSalaryLevel
                ]
                    ?.name,

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

    data class FilterResult(
        val filterType: String?,
        val users: List<AdminFilteredUserResponse>
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
        requestedFilter: String
    ): FilterResult {
        val normalizedFilter =
            normalizeValue(
                requestedFilter
            )

        if (normalizedFilter.isBlank()) {
            return FilterResult(
                filterType = null,
                users = emptyList()
            )
        }

        val rows =
            Accounts
                .selectAll()
                .where {
                    Accounts.isActive eq true
                }
                .toList()
                .sortedWith(
                    compareBy<ResultRow>(
                        { row ->
                            row[
                                Accounts.firstName
                            ]
                                ?.lowercase()
                                ?: ""
                        },
                        { row ->
                            row[
                                Accounts.lastName
                            ]
                                ?.lowercase()
                                ?: ""
                        },
                        { row ->
                            row[
                                Accounts.id
                            ].value
                        }
                    )
                )

        val allAccounts =
            mutableListOf<FilterableAccount>()

        for (row in rows) {
            allAccounts.add(
                rowToFilterableAccount(
                    row
                )
            )
        }


        val filterType =
            determineFilterType(
                accounts = allAccounts,
                requestedFilter =
                    normalizedFilter
            )

        if (filterType == null) {
            return FilterResult(
                filterType = null,
                users = emptyList()
            )
        }

        val filteredUsers =
            allAccounts
                .asSequence()
                .filter { account ->
                    accountMatchesFilter(
                        account = account,
                        requestedFilter =
                            normalizedFilter,
                        filterType =
                            filterType
                    )
                }
                .map { account ->
                    toFilteredUserResponse(
                        account
                    )
                }
                .toList()

        return FilterResult(
            filterType = filterType,
            users = filteredUsers
        )
    }



    private fun toFilteredUserResponse(
        account: AccountResponse
    ): AdminFilteredUserResponse {
        val firstName =
            account.firstName
                ?.trim()
                ?.takeIf { value ->
                    value.isNotBlank()
                }

        val lastName =
            account.lastName
                ?.trim()
                ?.takeIf { value ->
                    value.isNotBlank()
                }

        val generatedFullName =
            listOfNotNull(
                firstName,
                lastName
            )
                .joinToString(
                    separator = " "
                )
                .trim()
                .takeIf { value ->
                    value.isNotBlank()
                }

        val fullName =
            account.fullName
                ?.trim()
                ?.takeIf { value ->
                    value.isNotBlank()
                }
                ?: generatedFullName

        return AdminFilteredUserResponse(
            id = account.id,
            userId = account.userId,
            firstName = firstName,
            lastName = lastName,
            fullName = fullName,
            phoneNumber = account.phoneNumber,
            profilePictureUrl =
                account.profilePictureUrl
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



