package com.hr.account.helpers


import java.time.LocalDate
import java.time.Period

object AccountComputedValues {

    fun fullName(
        firstName: String?,
        middleName: String?,
        lastName: String?
    ): String {
        return listOfNotNull(
            firstName
                ?.trim()
                ?.takeIf { it.isNotBlank() },

            middleName
                ?.trim()
                ?.takeIf {
                    it.isNotBlank() &&
                            !it.equals(
                                other = "none",
                                ignoreCase = true
                            )
                },

            lastName
                ?.trim()
                ?.takeIf { it.isNotBlank() }
        ).joinToString(" ")
    }

    fun age(
        dateOfBirth: LocalDate?
    ): Int? {
        return dateOfBirth?.let {
            Period.between(
                it,
                LocalDate.now()
            ).years
        }
    }

    fun dateOfRetirement(
        dateOfBirth: LocalDate?,
        standardRetirementAge: Int
    ): LocalDate? {
        return dateOfBirth?.plusYears(
            standardRetirementAge.toLong()
        )
    }

    fun yearsOnCurrentGrade(
        dateOfLastPromotion: LocalDate?
    ): Int? {
        return dateOfLastPromotion?.let {
            Period.between(
                it,
                LocalDate.now()
            ).years
        }
    }

    fun numberOfYearsInService(
        dateOfFirstAppointment: LocalDate?
    ): Int? {
        return dateOfFirstAppointment?.let {
            Period.between(
                it,
                LocalDate.now()
            ).years
        }
    }

    fun displayName(
        fullName: String,
        userId: String?
    ): String {
        return when {
            fullName.isNotBlank() && !userId.isNullOrBlank() ->
                "$fullName - $userId"

            fullName.isNotBlank() ->
                fullName

            !userId.isNullOrBlank() ->
                userId

            else ->
                "Unknown account"
        }
    }
}
