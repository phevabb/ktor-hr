package com.hr.account.model


import java.time.LocalDate
import java.time.Period

object AccountComputedValues {

    fun calculateDateOfRetirement(
        dateOfBirth: LocalDate?,
        standardRetirementAge: Int = 60
    ): LocalDate? {

        return dateOfBirth?.plusYears(
            standardRetirementAge.toLong()
        )
    }

    fun calculateAge(
        dateOfBirth: LocalDate?
    ): Int? {

        if (dateOfBirth == null) {
            return null
        }

        return Period.between(
            dateOfBirth,
            LocalDate.now()
        ).years
    }

    fun calculateFullName(
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

    fun calculateYearsOnCurrentGrade(
        dateOfLastPromotion: LocalDate?
    ): Int? {

        if (dateOfLastPromotion == null) {
            return null
        }

        return Period.between(
            dateOfLastPromotion,
            LocalDate.now()
        ).years
    }

    fun calculateNumberOfYearsInService(
        dateOfFirstAppointment: LocalDate?
    ): Int? {

        if (dateOfFirstAppointment == null) {
            return null
        }

        return Period.between(
            dateOfFirstAppointment,
            LocalDate.now()
        ).years
    }

    fun accountDisplayName(
        firstName: String?,
        middleName: String?,
        lastName: String?,
        userId: String?
    ): String {

        val fullName = calculateFullName(
            firstName = firstName,
            middleName = middleName,
            lastName = lastName
        )

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
