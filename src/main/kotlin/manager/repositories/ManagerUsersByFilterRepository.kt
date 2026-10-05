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

    suspend fun getUsersByFilter(
        regionScopeId: Int?,
        requestedFilter: String
    ): ManagerUsersByFilterRepositoryResult {
        println(
            "=================================================="
        )

        println(
            "Manager filtered-users repository started"
        )

        println(
            "Manager region ID: $regionScopeId"
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
                            regionScopeId
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

                    regionScopeId =
                        regionScopeId
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