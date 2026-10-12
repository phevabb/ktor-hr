package com.hr.account.dtos

import kotlinx.serialization.Serializable

@Serializable
data class AccountListItemResponse(
    val id: Int,

    val isActive: Boolean,
    val isStaff: Boolean,
    val isSuperuser: Boolean,

    val academicQualificationId: Int?,
    val userId: String?,
    val professionalQualification: String?,

    val directorateId: Int?,
    val categoryId: Int?,
    val districtId: Int?,
    val regionId: Int?,
    val currentGradeId: Int?,
    val nextGradeId: Int?,
    val changeOfGradeId: Int?,
    val managementUnitCostCentreId: Int?,
    val titleId: Int?,
    val onLeaveTypeId: Int?,

    val firstName: String?,
    val lastName: String?,
    val middleName: String?,
    val maidenName: String?,
    val fullName: String?,

    val gender: Gender?,
    val atPostOnLeave: AtPostOnLeave?,
    val role: Role?,

    val dateOfBirth: String?,
    val standardRetirementAge: Int,
    val maritalStatus: MaritalStatus?,

    val professional: Professional?,
    val staffCategory: StaffCategory?,
    val fulltimeContractStaff: FulltimeContractStaff?,

    val currentSalaryPoint: SalaryPoint?,
    val currentSalaryLevel: SalaryLevel?,
    val nextSalaryLevel: SalaryLevel?,

    val dateOfAssumptionOfDuty: String?,
    val substantiveDate: String?,
    val nationalEffectiveDate: String?,
    val dateOfLastPromotion: String?,
    val dateOfFirstAppointment: String?,

    val singleSpineMonthlySalary: String?,
    val monthlyGrossPay: String?,
    val annualSalary: String?,

    val numberOfFocusAreas: Int?,
    val numberOfTargets: Int?,
    val numberOfTargetsMet: Int?,
    val numberOfTargetsNotMet: Int?,
    val overallAssessmentScore: String?,
    val selfAssessmentDescription: String?,

    val phoneNumber: String?,
    val email: String?,
    val ghanaCardNumber: String?,
    val socialSecurityNumber: String?,
    val nationalHealthInsuranceNumber: String?,

    val bankName: String?,
    val bankAccountNumber: String?,
    val bankAccountBranch: String?,

    val payrollStatus: PayrollStatus?,
    val accommodationStatus: AccommodationStatus?,
    val supervisorName: String?,

    val profilePictureUrl: String?,
    val profilePicturePublicId: String?,

    val dateJoined: String,
    val lastLogin: String?,

    val age: Int?,
    val dateOfRetirement: String?,
    val numberOfYearsInService: Int?,
    val yearsOnCurrentGrade: Int?
)