package com.hr.account.dtos



import kotlinx.serialization.Serializable

@Serializable
enum class Role {
    Admin,
    Manager,
    Staff
}

@Serializable
enum class Gender {
    Male,
    Female
}

@Serializable
enum class AtPostOnLeave {
    AT_POST,
    ON_LEAVE
}

@Serializable
enum class MaritalStatus {
    Single,
    Married
}

@Serializable
enum class Professional {
    Professional,
    Subprofessional
}

@Serializable
enum class StaffCategory {
    SENIOR_STAFF,
    JUNIOR_STAFF
}

@Serializable
enum class FulltimeContractStaff {
    FULLTIME,
    CONTRACT
}

@Serializable
enum class SalaryPoint {
    POINT_1,
    POINT_2,
    POINT_3,
    POINT_4,
    POINT_5,
    POINT_6,
    POINT_7,
    POINT_8,
    POINT_9,
    POINT_10,
    POINT_11,
    POINT_12,
    POINT_13,
    POINT_14,
    POINT_15
}

@Serializable
enum class SalaryLevel {
    SS_5,
    SS_6,
    SS_7,
    SS_8,
    SS_9,
    SS_10,
    SS_11,
    SS_12,
    SS_13,
    SS_14,
    SS_15,
    SS_16,
    SS_17,
    SS_18,
    SS_19,
    SS_20,
    SS_21,
    SS_22,
    SS_23,
    SS_24,
    SS_25
}

@Serializable
enum class AccommodationStatus {
    PERSONAL,
    RENTED,
    OFFICIAL_RESIDENCE_OASL,
    OFFICIAL_RESIDENCE_GOG
}

@Serializable
enum class PayrollStatus {
    ACTIVE_PAID,
    INACTIVE,
    SUSPENDED
}




enum class RoleChoices {
    Admin,
    Manager,
    Staff,

}





