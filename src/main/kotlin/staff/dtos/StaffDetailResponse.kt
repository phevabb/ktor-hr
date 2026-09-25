package com.hr.staff.dtos

import kotlinx.serialization.Serializable

@Serializable
data class StaffDetailResponse(
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

    val profilePictureUrl: String?,
    val profilePicturePublicId: String?,

    val phoneNumber: String?,
    val ghanaCardNumber: String?,
    val socialSecurityNumber: String?,
    val nationalHealthInsuranceNumber: String?,

    val dateOfBirth: String?,
    val standardRetirementAge: Int,

    val dateOfAssumptionOfDuty: String?,
    val substantiveDate: String?,
    val nationalEffectiveDate: String?,
    val dateOfLastPromotion: String?,
    val dateOfFirstAppointment: String?,

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
    val accommodationStatus: String?,
    val supervisorName: String?,
    val email: String?,


    val atPostOnLeave: String?,


    val nextGradeName: String?,


    val managementUnitCostCentreName: String?,


    val categoryName: String?,


    val titleName: String?,


    val onLeaveTypeName: String?,


    val dateJoined: String,
    val lastLogin: String?,

    val regionId: Int?,
    val regionName: String?,

    val districtId: Int?,
    val districtName: String?,

    val directorateId: Int?,
    val directorateName: String?,

    val currentGradeId: Int?,
    val currentGradeName: String?,

    val nextGradeId: Int?,

    val changeOfGradeId: Int?,
    val changeOfGradeName: String?,

    val managementUnitCostCentreId: Int?,

    val categoryId: Int?,

    val titleId: Int?,

    val onLeaveTypeId: Int?,

    val academicQualification:
    StaffAcademicQualificationResponse?
)

