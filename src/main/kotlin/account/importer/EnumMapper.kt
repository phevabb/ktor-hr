package com.hr.account.importer

import com.hr.account.dtos.AccommodationStatus
import com.hr.account.dtos.AtPostOnLeave
import com.hr.account.dtos.FulltimeContractStaff
import com.hr.account.dtos.Gender
import com.hr.account.dtos.MaritalStatus
import com.hr.account.dtos.PayrollStatus
import com.hr.account.dtos.Professional
import com.hr.account.dtos.Role
import com.hr.account.dtos.SalaryLevel
import com.hr.account.dtos.SalaryPoint
import com.hr.account.dtos.StaffCategory

object EnumMapper {

    fun role(value: String?): Role? {

        if (value.isNullOrBlank()) {
            return null
        }

        return Role.entries.firstOrNull {
            it.name.equals(
                value.trim(),
                ignoreCase = true
            )
        }
    }

    fun professional(
        value: String?
    ): Professional? {

        if (value.isNullOrBlank()) {
            return null
        }

        return Professional.entries.firstOrNull {
            it.name.equals(
                value.trim(),
                ignoreCase = true
            )
        }
    }

    fun gender(
        value: String?
    ): Gender? {

        if (value.isNullOrBlank()) {
            return null
        }

        return Gender.entries.firstOrNull {
            it.name.equals(
                value.trim(),
                ignoreCase = true
            )
        }
    }

    fun maritalStatus(
        value: String?
    ): MaritalStatus? {

        if (value.isNullOrBlank()) {
            return null
        }

        return MaritalStatus.entries.firstOrNull {
            it.name.equals(
                value.trim(),
                ignoreCase = true
            )
        }
    }

    fun payrollStatus(
        value: String?
    ): PayrollStatus? {

        if (value.isNullOrBlank()) {
            return null
        }

        return PayrollStatus.entries.firstOrNull {
            it.name.equals(
                value.trim(),
                ignoreCase = true
            )
        }
    }

    fun accommodationStatus(
        value: String?
    ): AccommodationStatus? {

        if (value.isNullOrBlank()) {
            return null
        }

        return AccommodationStatus.entries.firstOrNull {
            it.name.equals(
                value.trim(),
                ignoreCase = true
            )
        }
    }

    fun atPostOnLeave(
        value: String?
    ): AtPostOnLeave? {

        if (value.isNullOrBlank()) {
            return null
        }

        return AtPostOnLeave.entries.firstOrNull {
            it.name.equals(
                value.trim(),
                ignoreCase = true
            )
        }
    }

    fun fulltimeContractStaff(
        value: String?
    ): FulltimeContractStaff? {

        if (value.isNullOrBlank()) {
            return null
        }

        return FulltimeContractStaff.entries.firstOrNull {
            it.name.equals(
                value.trim(),
                ignoreCase = true
            )
        }
    }

    fun staffCategory(
        value: String?
    ): StaffCategory? {

        if (value.isNullOrBlank()) {
            return null
        }

        return StaffCategory.entries.firstOrNull {
            it.name.equals(
                value.trim(),
                ignoreCase = true
            )
        }
    }

    fun salaryLevel(
        value: String?
    ): SalaryLevel? {

        if (value.isNullOrBlank()) {
            return null
        }

        return SalaryLevel.entries.firstOrNull {
            it.name.equals(
                value.trim(),
                ignoreCase = true
            )
        }
    }

    fun salaryPoint(
        value: String?
    ): SalaryPoint? {

        if (value.isNullOrBlank()) {
            return null
        }

        return SalaryPoint.entries.firstOrNull {
            it.name.equals(
                value.trim(),
                ignoreCase = true
            )
        }
    }
}
