package com.hr.admin.excel.dto

import kotlinx.serialization.Serializable

@Serializable
data class AdminUserExcelResponse(
    val id: Int,

    val email: String?,
    val userId: String?,

    val firstName: String?,
    val middleName: String?,
    val lastName: String?,
    val fullName: String,
    val title: String?,

    val role: String?,
    val gender: String?,

    val dateOfBirth: String?,
    val age: Int?,
    val maritalStatus: String?,

    val professional: String?,
    val professionalQualification: String?,
    val staffCategory: String?,
    val fulltimeContractStaff: String?,

    val managementUnitCostCentre: String?,
    val directorate: String?,
    val category: String?,
    val district: String?,
    val region: String?,

    val currentGrade: String?,
    val nextGrade: String?,
    val changeOfGrade: String?,

    val academicQualifications: String,

    val dateOfAssumptionOfDuty: String?,
    val dateOfRetirement: String?,
    val dateOfFirstAppointment: String?,
    val dateOfLastPromotion: String?,
    val nationalEffectiveDate: String?,
    val substantiveDate: String?,

    val numberOfYearsInService: Int?,
    val yearsOnCurrentGrade: Int?,

    val currentSalaryLevel: String?,
    val currentSalaryPoint: String?,
    val nextSalaryLevel: String?,

    val singleSpineMonthlySalary: String?,
    val monthlyGrossPay: String?,
    val annualSalary: String?,

    val phoneNumber: String?,
    val ghanaCardNumber: String?,
    val socialSecurityNumber: String?,
    val nationalHealthInsuranceNumber: String?,

    val bankName: String?,
    val bankAccountBranch: String?,
    val bankAccountNumber: String?,

    val payrollStatus: String?,
    val accommodationStatus: String?,
    val supervisorName: String?,
    val atPostOnLeave: String?,

    val selfAssessmentDescription: String?,
    val overallAssessmentScore: String?,

    val numberOfTargets: Int?,
    val numberOfTargetsMet: Int?,
    val numberOfTargetsNotMet: Int?,
    val numberOfFocusAreas: Int?,

    val profilePicture: String?
)