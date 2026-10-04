package com.hr.manager.repositories

import com.hr.account.dtos.AccountResponse
import com.hr.account.repositories.AccountRepository
import com.hr.account.table.Accounts
import com.hr.manager.dtos.ManagerUserExcelResponse
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagerUsersExcelRepository {

    suspend fun getActiveUsersForExcel(
        regionId: Int
    ): List<ManagerUserExcelResponse> {
        println(
            "=================================================="
        )

        println(
            "Retrieving active Manager-region accounts for Excel"
        )

        println(
            "Manager region ID: $regionId"
        )

        val rows =
            Accounts
                .selectAll()
                .where {
                    Accounts.isActive eq true
                }
                .toList()
                .filter { row ->
                    row[
                        Accounts.regionId
                    ]
                        ?.value ==
                        regionId
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
                                Accounts.userId
                            ]
                                ?.lowercase()
                        }
                    )
                )

        println(
            "Active account rows found for Excel: ${rows.size}"
        )

        val exportUsers =
            mutableListOf<ManagerUserExcelResponse>()

        for (row in rows) {
            val account =
                AccountRepository
                    .rowToAccountResponse(
                        row
                    )

            exportUsers.add(
                account.toManagerExcelResponse()
            )
        }

        println(
            "Manager Excel records created: ${exportUsers.size}"
        )

        println(
            "=================================================="
        )

        return exportUsers
    }

    private fun AccountResponse.toManagerExcelResponse():
        ManagerUserExcelResponse {
        return ManagerUserExcelResponse(
            id =
                id,

            userId =
                userId,

            fullName =
                fullName,

            firstName =
                firstName,

            middleName =
                middleName,

            lastName =
                lastName,

            email =
                email,

            phoneNumber =
                phoneNumber,

            role =
                role,

            gender =
                gender,

            maritalStatus =
                maritalStatus,

            professional =
                professional,

            professionalQualification =
                professionalQualification,

            staffCategory =
                staffCategory,

            fulltimeContractStaff =
                fulltimeContractStaff,

            dateOfBirth =
                dateOfBirth,

            age =
                age,

            dateOfRetirement =
                dateOfRetirement,

            dateOfFirstAppointment =
                dateOfFirstAppointment,

            numberOfYearsInService =
                numberOfYearsInService,

            dateOfLastPromotion =
                dateOfLastPromotion,

            yearsOnCurrentGrade =
                yearsOnCurrentGrade,

            currentSalaryLevel =
                currentSalaryLevel,

            currentSalaryPoint =
                currentSalaryPoint,

            nextSalaryLevel =
                nextSalaryLevel,

            academicQualification =
                academicQualification
                    ?.name,

            directorateName =
                directorateName,

            categoryName =
                categoryName,

            districtName =
                districtName,

            regionName =
                regionName,

            currentGradeName =
                currentGradeName,

            nextGradeName =
                nextGradeName,

            changeOfGradeName =
                changeOfGradeName,

            managementUnitCostCentreName =
                managementUnitCostCentreName,

            titleName =
                titleName,

            onLeaveTypeName =
                onLeaveTypeName,

            payrollStatus =
                payrollStatus,

            accommodationStatus =
                accommodationStatus,

            atPostOnLeave =
                atPostOnLeave,

            supervisorName =
                supervisorName,

            isActive =
                isActive
        )
    }
}