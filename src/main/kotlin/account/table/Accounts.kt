package com.hr.account.table


import com.hr.academicqualification.table.AcademicQualifications
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
import com.hr.changeofgrade.tables.ChangeOfGrades
import com.hr.currentgrade.tables.CurrentGrades
import com.hr.department.table.Departments
import com.hr.districts.tables.Districts
import com.hr.region.tables.Regions
import com.hr.managementUnits.tables.ManagementUnits
import com.hr.nextgrade.tables.NextGrades
import com.hr.onleavetype.table.OnLeaveTypes
import com.hr.staffclass.table.StaffClasses
import com.hr.title.table.Titles
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import java.time.LocalDateTime
import org.jetbrains.exposed.v1.javatime.date
import org.jetbrains.exposed.v1.javatime.datetime


object Accounts : IntIdTable("accounts") {


    val academicQualificationId = reference(
        name = "academic_qualification_id",
        foreign = AcademicQualifications,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()

    val userId = varchar("user_id", 222)
        .uniqueIndex()
        .nullable()

    val passwordHash = varchar("password_hash", 255)

    val professionalQualification = varchar(
        name = "professional_qualification",
        length = 100
    ).nullable()


    val isActive = bool("is_active")
        .default(true)

    val isStaff = bool("is_staff")
        .default(false)

    val isSuperuser = bool("is_superuser")
        .default(false)

    /*
     * Table relationships
     */

    val directorateId = reference(
        name = "directorate_id",
        foreign = Departments,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()

    val categoryId = reference(
        name = "category_id",
        foreign = StaffClasses,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()

    val districtId = reference(
        name = "district_id",
        foreign = Districts,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()

    val regionId = reference(
        name = "region_id",
        foreign = Regions,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()

    val currentGradeId = reference(
        name = "current_grade_id",
        foreign = CurrentGrades,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()

    val nextGradeId = reference(
        name = "next_grade_id",
        foreign = NextGrades,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()

    val changeOfGradeId = reference(
        name = "change_of_grade_id",
        foreign = ChangeOfGrades,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()

    val managementUnitCostCentreId = reference(
        name = "management_unit_cost_centre_id",
        foreign = ManagementUnits,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()

    val titleId = reference(
        name = "title_id",
        foreign = Titles,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()

    val onLeaveTypeId = reference(
        name = "on_leave_type_id",
        foreign = OnLeaveTypes,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()

    /*
     * Personal information
     */

    val firstName = varchar("first_name", 222)
        .nullable()

    val lastName = varchar("last_name", 222)
        .nullable()

    val middleName = varchar("middle_name", 222)
        .nullable()

    val maidenName = varchar("maiden_name", 222)
        .nullable()

    val gender = enumerationByName(
        name = "gender",
        length = 10,
        klass = Gender::class
    ).nullable()

    val atPostOnLeave = enumerationByName(
        name = "at_post_on_leave",
        length = 50,
        klass = AtPostOnLeave::class
    ).nullable()


    val role = enumerationByName(
        name = "role",
        length = 50,
        klass = Role::class
    ).nullable()



    val dateOfBirth = date("date_of_birth")
        .index()
        .nullable()


    val standardRetirementAge = integer("standard_retirement_age")
        .default(60)


    val maritalStatus = enumerationByName(
        name = "marital_status",
        length = 50,
        klass = MaritalStatus::class
    ).nullable()

    /*
     * Employment and professional information
     */

    val professional = enumerationByName(
        name = "professional",
        length = 100,
        klass = Professional::class
    ).nullable()

    val staffCategory = enumerationByName(
        name = "staff_category",
        length = 100,
        klass = StaffCategory::class
    ).nullable()



    val fulltimeContractStaff = enumerationByName(
        name = "fulltime_contract_staff",
        length = 100,
        klass = FulltimeContractStaff::class
    ).nullable()



    val currentSalaryPoint = enumerationByName(
        name = "current_salary_point",
        length = 50,
        klass = SalaryPoint::class
    ).nullable()

    val currentSalaryLevel = enumerationByName(
        name = "current_salary_level",
        length = 50,
        klass = SalaryLevel::class
    ).nullable()

    val nextSalaryLevel = enumerationByName(
        name = "next_salary_level",
        length = 50,
        klass = SalaryLevel::class
    ).nullable()

    val dateOfAssumptionOfDuty = date(
        "date_of_assumption_of_duty"
    ).nullable()

    val substantiveDate = date(
        "substantive_date"
    ).nullable()

    val nationalEffectiveDate = date(
        "national_effective_date"
    ).nullable()

    val dateOfLastPromotion = date(
        "date_of_last_promotion"
    ).nullable()

    val dateOfFirstAppointment = date(
        "date_of_first_appointment"
    ).nullable()

    /*
     * Salary information
     */

    val singleSpineMonthlySalary = decimal(
        name = "single_spine_monthly_salary",
        precision = 10,
        scale = 2
    ).nullable()

    val monthlyGrossPay = decimal(
        name = "monthly_gross_pay",
        precision = 10,
        scale = 2
    ).nullable()

    val annualSalary = decimal(
        name = "annual_salary",
        precision = 10,
        scale = 2
    ).nullable()

    /*
     * Performance information
     */

    val numberOfFocusAreas = integer(
        "number_of_focus_areas"
    ).nullable()

    val numberOfTargets = integer(
        "number_of_targets"
    ).nullable()

    val numberOfTargetsMet = integer(
        "number_of_targets_met"
    ).nullable()

    val numberOfTargetsNotMet = integer(
        "number_of_targets_not_met"
    ).nullable()

    val overallAssessmentScore = decimal(
        name = "overall_assessment_score",
        precision = 5,
        scale = 2
    ).nullable()

    val selfAssessmentDescription = text(
        "self_assessment_description"
    ).nullable()

    /*
     * Contact and identification information
     */

    val phoneNumber = varchar("phone_number", 222)
        .uniqueIndex()
        .nullable()

    val ghanaCardNumber = varchar("ghana_card_number", 222)
        .uniqueIndex()
        .nullable()

    val socialSecurityNumber = varchar(
        "social_security_number",
        222
    ).uniqueIndex().nullable()

    val nationalHealthInsuranceNumber = varchar(
        "national_health_insurance_number",
        222
    ).uniqueIndex().nullable()

    /*
     * Banking information
     */

    val bankName = varchar("bank_name", 222)
        .nullable()

    val bankAccountNumber = varchar(
        "bank_account_number",
        222
    ).uniqueIndex().nullable()

    val bankAccountBranch = varchar(
        "bank_account_branch",
        100
    ).nullable()

    /*
     * Payroll, leave and accommodation
     */

    val payrollStatus = enumerationByName(
        name = "payroll_status",
        length = 50,
        klass = PayrollStatus::class
    ).nullable()



    val accommodationStatus = enumerationByName(
        name = "accommodation_status",
        length = 100,
        klass = AccommodationStatus::class
    ).nullable()

    val supervisorName = varchar(
        "supervisor_name",
        100
    ).nullable()

    /*
     * Profile picture
     */

    val profilePictureUrl = varchar(
        "profile_picture_url",
        500
    ).nullable()

    val profilePicturePublicId = varchar(
        "profile_picture_public_id",
        255
    ).nullable()

    /*
     * Dates
     */

    val dateJoined = datetime("date_joined")
        .clientDefault {
            LocalDateTime.now()
        }

    val lastLogin = datetime("last_login")
        .nullable()
}