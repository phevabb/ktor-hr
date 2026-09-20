package com.hr.account.dtos

import kotlinx.serialization.Serializable

@Serializable
data class AccountCreateRequest(
    val userId: String,
    val role: Role,

    val firstName: String? = null,
    val middleName: String? = null,
    val lastName: String? = null,
    val maidenName: String? = null,

    val gender: Gender? = null,
    val dateOfBirth: String? = null,
    val maritalStatus: MaritalStatus? = null,

    val directorateId: Int? = null,
    val categoryId: Int? = null,
    val districtId: Int? = null,
    val regionId: Int? = null,
    val currentGradeId: Int? = null,
    val nextGradeId: Int? = null,
    val changeOfGradeId: Int? = null,
    val managementUnitCostCentreId: Int? = null,
    val titleId: Int? = null,
    val onLeaveTypeId: Int? = null,

    val professional: Professional? = null,
    val professionalQualification: String? = null,
    val staffCategory: StaffCategory? = null,
    val fulltimeContractStaff: FulltimeContractStaff? = null,

    val currentSalaryLevel: SalaryLevel? = null,
    val currentSalaryPoint: SalaryPoint? = null,
    val nextSalaryLevel: SalaryLevel? = null,

    val dateOfAssumptionOfDuty: String? = null,
    val substantiveDate: String? = null,
    val nationalEffectiveDate: String? = null,
    val dateOfLastPromotion: String? = null,
    val dateOfFirstAppointment: String? = null,

    val singleSpineMonthlySalary: String? = null,
    val monthlyGrossPay: String? = null,
    val annualSalary: String? = null,
    val academicQualificationId: Int? = null,

    val numberOfFocusAreas: Int? = null,
    val numberOfTargets: Int? = null,
    val numberOfTargetsMet: Int? = null,
    val numberOfTargetsNotMet: Int? = null,
    val overallAssessmentScore: String? = null,
    val selfAssessmentDescription: String? = null,

    val phoneNumber: String? = null,
    val ghanaCardNumber: String? = null,
    val socialSecurityNumber: String? = null,
    val nationalHealthInsuranceNumber: String? = null,

    val bankName: String? = null,
    val bankAccountNumber: String? = null,
    val bankAccountBranch: String? = null,

    val payrollStatus: PayrollStatus? = null,
    val atPostOnLeave: AtPostOnLeave? = null,
    val accommodationStatus: AccommodationStatus? = null,
    val supervisorName: String? = null,

    val profilePictureUrl: String? = null,
    val profilePicturePublicId: String? = null,

    val standardRetirementAge: Int = 60,
    val isActive: Boolean = true,
    val isStaff: Boolean = false,
    val isSuperuser: Boolean = false
)

@Serializable


data class AccountResponse(
    val id: Int,
    val userId: String?,
    val role: Role?,

    val firstName: String?,
    val middleName: String?,
    val lastName: String?,
    val fullName: String,
    val displayName: String,

    val maidenName: String? = null,
    val gender: Gender? = null,
    val maritalStatus: MaritalStatus? = null,

    val dateOfBirth: String? = null,
    val age: Int? = null,
    val standardRetirementAge: Int = 60,
    val dateOfRetirement: String? = null,

    val directorateId: Int? = null,
    val categoryId: Int? = null,
    val districtId: Int? = null,
    val regionId: Int? = null,
    val academicQualificationId: Int? = null,
    val currentGradeId: Int? = null,
    val nextGradeId: Int? = null,
    val changeOfGradeId: Int? = null,
    val managementUnitCostCentreId: Int? = null,
    val titleId: Int? = null,
    val onLeaveTypeId: Int? = null,

    val professional: Professional? = null,
    val professionalQualification: String? = null,
    val staffCategory: StaffCategory? = null,
    val fulltimeContractStaff: FulltimeContractStaff? = null,

    val currentSalaryLevel: SalaryLevel? = null,
    val currentSalaryPoint: SalaryPoint? = null,
    val nextSalaryLevel: SalaryLevel? = null,

    val dateOfAssumptionOfDuty: String? = null,
    val substantiveDate: String? = null,
    val nationalEffectiveDate: String? = null,

    val dateOfLastPromotion: String? = null,
    val yearsOnCurrentGrade: Int? = null,

    val dateOfFirstAppointment: String? = null,
    val numberOfYearsInService: Int? = null,

    val singleSpineMonthlySalary: String? = null,
    val monthlyGrossPay: String? = null,
    val annualSalary: String? = null,

    val numberOfFocusAreas: Int? = null,
    val numberOfTargets: Int? = null,
    val numberOfTargetsMet: Int? = null,
    val numberOfTargetsNotMet: Int? = null,
    val overallAssessmentScore: String? = null,
    val selfAssessmentDescription: String? = null,

    val phoneNumber: String? = null,
    val ghanaCardNumber: String? = null,
    val socialSecurityNumber: String? = null,
    val nationalHealthInsuranceNumber: String? = null,

    val bankName: String? = null,
    val bankAccountNumber: String? = null,
    val bankAccountBranch: String? = null,

    val payrollStatus: PayrollStatus? = null,
    val atPostOnLeave: AtPostOnLeave? = null,
    val accommodationStatus: AccommodationStatus? = null,
    val supervisorName: String? = null,

    val profilePictureUrl: String? = null,
    val profilePicturePublicId: String? = null,

    val isActive: Boolean = true,
    val isStaff: Boolean = false,
    val isSuperuser: Boolean = false,

    val dateJoined: String,
    val lastLogin: String? = null
)