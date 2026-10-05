package com.hr.manager.repositories

import com.hr.account.dtos.AccountResponse
import com.hr.account.repositories.AccountRepository
import com.hr.account.table.Accounts
import java.util.Locale
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagerUsersByFilterRepository {

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
        managerRegionId: Int,
        requestedFilter: String
    ): ManagerUsersByFilterRepositoryResult {
        println(
            "=================================================="
        )

        println(
            "Manager filtered-users repository started"
        )

        println(
            "Manager region ID: $managerRegionId"
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
                "=================================================="
            )

            return ManagerUsersByFilterRepositoryResult(
                filterType =
                    null,

                users =
                    emptyList()
            )
        }

        val accountRows =
            Accounts
                .selectAll()
                .where {
                    Accounts.isActive eq
                            true
                }
                .toList()
                .filter { row ->
                    row[
                        Accounts.regionId
                    ]
                        ?.value ==
                            managerRegionId
                }
                .sortedWith(
                    compareBy<ResultRow>(
                        {
                                row ->

                            row[
                                Accounts.firstName
                            ]
                                ?.lowercase()
                                ?: ""
                        },
                        {
                                row ->

                            row[
                                Accounts.lastName
                            ]
                                ?.lowercase()
                                ?: ""
                        },
                        {
                                row ->

                            row[
                                Accounts.id
                            ].value
                        }
                    )
                )

        println(
            "Active accounts found in Manager region: ${accountRows.size}"
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
            "Regional accounts mapped: ${regionalAccounts.size}"
        )

        val filterType =
            determineFilterType(
                accounts =
                    regionalAccounts,

                requestedFilter =
                    normalizedFilter
            )

        println(
            "Detected filter type: ${filterType ?: "None"}"
        )

        if (filterType == null) {
            println(
                "The requested filter did not match a supported category"
            )

            println(
                "=================================================="
            )

            return ManagerUsersByFilterRepositoryResult(
                filterType =
                    null,

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

                    managerRegionId =
                        managerRegionId
                )
            }

        println(
            "Filtered active accounts: ${filteredAccounts.size}"
        )

        filteredAccounts.forEach { account ->
            println(
                "Matched account: " +
                        "id=${account.id}, " +
                        "userId=${account.userId}, " +
                        "fullName=${account.fullName}"
            )
        }

        println(
            "=================================================="
        )

        return ManagerUsersByFilterRepositoryResult(
            filterType =
                filterType,

            users =
                filteredAccounts
        )
    }

    private fun determineFilterType(
        accounts: List<AccountResponse>,
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
            requestedFilter in
            setOf(
                "NO",
                "STUDY LEAVE WITH PAY",
                "STUDY LEAVE WITHOUT PAY"
            )
        ) {
            return "leave type"
        }

        if (
            requestedFilter in
            setOf(
                "FULLTIME",
                "CONTRACT"
            )
        ) {
            return "agreement type"
        }

        if (
            requestedFilter in
            setOf(
                "PROFESSIONAL",
                "SUBPROFESSIONAL"
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
        account: AccountResponse,
        requestedFilter: String,
        filterType: String,
        managerRegionId: Int
    ): Boolean {
        /*
         * Keep this additional region validation even though
         * the records were already filtered by region.
         */
        if (
            account.regionId !=
            managerRegionId
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
        return normalizeValue(
            actualValue
        ) ==
                normalizeValue(
                    requestedValue
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