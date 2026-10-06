package com.hr.admin.dtos

data class AdminUserUpdateRequest(
    val providedFields:
    Set<String> =
        emptySet(),

    val userId:
    String? =
        null,

    val email:
    String? =
        null,

    val firstName:
    String? =
        null,

    val middleName:
    String? =
        null,

    val lastName:
    String? =
        null,

    val maidenName:
    String? =
        null,

    val role:
    String? =
        null,

    val professional:
    String? =
        null,

    val gender:
    String? =
        null,

    val maritalStatus:
    String? =
        null,

    val staffCategory:
    String? =
        null,

    val fulltimeContractStaff:
    String? =
        null,

    val atPostOnLeave:
    String? =
        null,

    val payrollStatus:
    String? =
        null,

    val accommodationStatus:
    String? =
        null,

    val currentSalaryLevel:
    String? =
        null,

    val currentSalaryPoint:
    String? =
        null,

    val nextSalaryLevel:
    String? =
        null,

    val currentGradeId:
    Int? =
        null,

    val nextGradeId:
    Int? =
        null,

    val changeOfGradeId:
    Int? =
        null,

    val managementUnitCostCentreId:
    Int? =
        null,

    val directorateId:
    Int? =
        null,

    val categoryId:
    Int? =
        null,

    val districtId:
    Int? =
        null,

    val regionId:
    Int? =
        null,

    val titleId:
    Int? =
        null,

    val onLeaveTypeId:
    Int? =
        null,

    /*
     * Your current Ktor Accounts table uses one
     * academicQualificationId foreign key.
     *
     * If the frontend sends multiple IDs, the route
     * uses the first valid ID.
     */
    val academicQualificationId:
    Int? =
        null,

    val dateOfBirth:
    String? =
        null,

    val dateOfAssumptionOfDuty:
    String? =
        null,

    val substantiveDate:
    String? =
        null,

    val nationalEffectiveDate:
    String? =
        null,

    val dateOfFirstAppointment:
    String? =
        null,

    val dateOfLastPromotion:
    String? =
        null,

    val professionalQualification:
    String? =
        null,

    val phoneNumber:
    String? =
        null,

    val ghanaCardNumber:
    String? =
        null,

    val socialSecurityNumber:
    String? =
        null,

    val nationalHealthInsuranceNumber:
    String? =
        null,

    val bankName:
    String? =
        null,

    val bankAccountBranch:
    String? =
        null,

    val bankAccountNumber:
    String? =
        null,

    val supervisorName:
    String? =
        null,

    val selfAssessmentDescription:
    String? =
        null,

    val profilePictureUrl:
    String? =
        null,

    val profilePicturePublicId:
    String? =
        null,

    val standardRetirementAge:
    Int? =
        null,

    val numberOfTargets:
    Int? =
        null,

    val numberOfTargetsMet:
    Int? =
        null,

    val numberOfTargetsNotMet:
    Int? =
        null,

    val numberOfFocusAreas:
    Int? =
        null,

    val singleSpineMonthlySalary:
    String? =
        null,

    val monthlyGrossPay:
    String? =
        null,

    val annualSalary:
    String? =
        null,

    val overallAssessmentScore:
    String? =
        null,

    val removeProfilePicture:
    Boolean =
        false
)