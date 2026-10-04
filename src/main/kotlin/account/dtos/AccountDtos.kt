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

    val email: String? = null,

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

    val firstName: String?,
    val middleName: String?,
    val lastName: String?,
    val maidenName: String?,

    val fullName: String,
    val displayName: String,

    val role: String?,
    val gender: String?,
    val maritalStatus: String?,

    val professional: String?,
    val professionalQualification: String?,
    val staffCategory: String?,
    val fulltimeContractStaff: String?,

    val isActive: Boolean,
    val isStaff: Boolean,
    val isSuperuser: Boolean,

    val email: String?,
    val phoneNumber: String?,

    val profilePictureUrl: String?,
    val profilePicturePublicId: String?,

    val ghanaCardNumber: String?,
    val socialSecurityNumber: String?,
    val nationalHealthInsuranceNumber: String?,

    val dateOfBirth: String?,
    val age: Int?,
    val standardRetirementAge: Int,
    val dateOfRetirement: String?,

    val dateOfAssumptionOfDuty: String?,
    val substantiveDate: String?,
    val nationalEffectiveDate: String?,
    val dateOfLastPromotion: String?,
    val dateOfFirstAppointment: String?,

    val yearsOnCurrentGrade: Int?,
    val numberOfYearsInService: Int?,

    val currentSalaryPoint: String?,
    val currentSalaryLevel: String?,
    val nextSalaryLevel: String?,

    val singleSpineMonthlySalary: String?,
    val monthlyGrossPay: String?,
    val annualSalary: String?,

    val numberOfFocusAreas: Int?,
    val numberOfTargets: Int?,
    val numberOfTargetsMet: Int?,
    val numberOfTargetsNotMet: Int?,
    val overallAssessmentScore: String?,
    val selfAssessmentDescription: String?,

    val bankName: String?,
    val bankAccountNumber: String?,
    val bankAccountBranch: String?,

    val payrollStatus: String?,
    val atPostOnLeave: String?,
    val accommodationStatus: String?,
    val supervisorName: String?,

    val academicQualificationId: Int?,
    val academicQualification:
    AccountAcademicQualificationResponse?,

    val directorateId: Int?,
    val directorateName: String?,

    val categoryId: Int?,
    val categoryName: String?,

    val districtId: Int?,
    val districtName: String?,

    val regionId: Int?,
    val regionName: String?,

    val currentGradeId: Int?,
    val currentGradeName: String?,

    val nextGradeId: Int?,
    val nextGradeName: String?,

    val changeOfGradeId: Int?,
    val changeOfGradeName: String?,

    val managementUnitCostCentreId: Int?,
    val managementUnitCostCentreName: String?,

    val titleId: Int?,
    val titleName: String?,

    val onLeaveTypeId: Int?,
    val onLeaveTypeName: String?,

    val dateJoined: String,
    val lastLogin: String?
)




