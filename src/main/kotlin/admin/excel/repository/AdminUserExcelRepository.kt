package com.hr.admin.excel.repository


import com.hr.account.dtos.AccountResponse
import com.hr.account.repositories.AccountRepository
import com.hr.account.table.Accounts
import com.hr.admin.excel.dto.AdminUserExcelResponse
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object AdminUserExcelRepository {

    suspend fun getAllActiveUsers():
            List<AdminUserExcelResponse> {

        println(
            "Admin Excel export repository started"
        )

        val startedAt =
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
                .toList()

        println(
            "Active account rows retrieved: ${rows.size}"
        )

        val exportRecords =
            rows.mapIndexed { index, row ->
                val accountId =
                    row[
                        Accounts.id
                    ].value

                println(
                    "Preparing Excel account ${index + 1} of ${rows.size}: $accountId"
                )

                val account =
                    AccountRepository
                        .rowToAccountResponse(
                            row
                        )

                accountToExcelResponse(
                    account
                )
            }

        println(
            "Admin Excel export records prepared: ${exportRecords.size}"
        )

        println(
            "Admin Excel export repository duration: ${
                System.currentTimeMillis() -
                        startedAt
            } ms"
        )

        return exportRecords
    }

    private fun accountToExcelResponse(
        account: AccountResponse
    ): AdminUserExcelResponse {
        return AdminUserExcelResponse(
            id =
                account.id,

            email =
                account.email,

            userId =
                account.userId,

            firstName =
                account.firstName,

            middleName =
                account.middleName,

            lastName =
                account.lastName,

            fullName =
                account.fullName,

            title =
                account.titleName,

            role =
                account.role,

            gender =
                account.gender,

            dateOfBirth =
                account.dateOfBirth,

            age =
                account.age,

            maritalStatus =
                account.maritalStatus,

            professional =
                account.professional,

            professionalQualification =
                account.professionalQualification,

            staffCategory =
                account.staffCategory,

            fulltimeContractStaff =
                account.fulltimeContractStaff,

            managementUnitCostCentre =
                account.managementUnitCostCentreName,

            directorate =
                account.directorateName,

            category =
                account.categoryName,

            district =
                account.districtName,

            region =
                account.regionName,

            currentGrade =
                account.currentGradeName,

            nextGrade =
                account.nextGradeName,

            changeOfGrade =
                account.changeOfGradeName,

            academicQualifications =
                formatAcademicQualifications(
                    account
                ),

            dateOfAssumptionOfDuty =
                account.dateOfAssumptionOfDuty,

            dateOfRetirement =
                account.dateOfRetirement,

            dateOfFirstAppointment =
                account.dateOfFirstAppointment,

            dateOfLastPromotion =
                account.dateOfLastPromotion,

            nationalEffectiveDate =
                account.nationalEffectiveDate,

            substantiveDate =
                account.substantiveDate,

            numberOfYearsInService =
                account.numberOfYearsInService,

            yearsOnCurrentGrade =
                account.yearsOnCurrentGrade,

            currentSalaryLevel =
                account.currentSalaryLevel,

            currentSalaryPoint =
                account.currentSalaryPoint,

            nextSalaryLevel =
                account.nextSalaryLevel,

            singleSpineMonthlySalary =
                account.singleSpineMonthlySalary,

            monthlyGrossPay =
                account.monthlyGrossPay,

            annualSalary =
                account.annualSalary,

            phoneNumber =
                account.phoneNumber,

            ghanaCardNumber =
                account.ghanaCardNumber,

            socialSecurityNumber =
                account.socialSecurityNumber,

            nationalHealthInsuranceNumber =
                account.nationalHealthInsuranceNumber,

            bankName =
                account.bankName,

            bankAccountBranch =
                account.bankAccountBranch,

            bankAccountNumber =
                account.bankAccountNumber,

            payrollStatus =
                account.payrollStatus,

            accommodationStatus =
                account.accommodationStatus,

            supervisorName =
                account.supervisorName,

            atPostOnLeave =
                account.atPostOnLeave,

            selfAssessmentDescription =
                account.selfAssessmentDescription,

            overallAssessmentScore =
                account.overallAssessmentScore,

            numberOfTargets =
                account.numberOfTargets,

            numberOfTargetsMet =
                account.numberOfTargetsMet,

            numberOfTargetsNotMet =
                account.numberOfTargetsNotMet,

            numberOfFocusAreas =
                account.numberOfFocusAreas,

            profilePicture =
                account.profilePictureUrl
        )
    }

    private fun formatAcademicQualifications(
        account: AccountResponse
    ): String {
        return account
            .academicQualification
            ?.name
            ?.trim()
            .orEmpty()
    }
}