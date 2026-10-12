package com.hr.account.importer

import com.hr.account.table.Accounts
import com.hr.academicqualification.table.AcademicQualifications
import com.hr.changeofgrade.tables.ChangeOfGrades
import com.hr.classes.tables.Classes
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.currentgrade.tables.CurrentGrades
import com.hr.department.table.Departments
import com.hr.districts.tables.Districts
import com.hr.managementUnits.tables.ManagementUnits
import com.hr.nextgrade.tables.NextGrades
import com.hr.region.tables.Regions
import com.hr.title.table.Titles
import kotlinx.coroutines.flow.any
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll

import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object ExcelAccountImporter {

    private const val DEFAULT_PASSWORD =
        "Securepassword123!"

    private val dataFormatter =
        DataFormatter()

    private val supportedDateFormats = listOf(
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("d/M/yyyy"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        DateTimeFormatter.ofPattern("M/d/yyyy"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("d-M-yyyy"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),
        DateTimeFormatter.ofPattern("dd MMM yyyy"),
        DateTimeFormatter.ofPattern("d MMM yyyy"),
        DateTimeFormatter.ofPattern("dd-MMM-yyyy"),
        DateTimeFormatter.ofPattern("d-MMM-yyyy")
    )

    suspend fun importExcel(
        filePath: String
    ): ImportResult {

        val excelFile = File(filePath)

        if (!excelFile.exists()) {
            throw IllegalArgumentException(
                "Excel file was not found: ${excelFile.absolutePath}"
            )
        }

        var totalRows = 0
        var importedRows = 0
        var skippedRows = 0

        val errors = mutableListOf<String>()

        WorkbookFactory.create(excelFile).use { workbook ->

            val sheet = workbook.getSheetAt(0)

            val headerRow = sheet.getRow(0)
                ?: throw IllegalArgumentException(
                    "The Excel file does not contain a header row."
                )

            val headers =
                createHeaderMap(headerRow)

            validateRequiredHeaders(headers)

            totalRows = sheet.lastRowNum

            for (rowIndex in 1..sheet.lastRowNum) {

                val row = sheet.getRow(rowIndex)

                if (row == null || isRowEmpty(row)) {
                    skippedRows++
                    continue
                }

                try {

                    val imported = importRow(
                        row = row,
                        headers = headers
                    )

                    if (imported) {
                        importedRows++
                    } else {
                        skippedRows++
                    }

                } catch (exception: Exception) {

                    skippedRows++

                    val userId = getCell(
                        row = row,
                        headers = headers,
                        columnName = "user_id"
                    )

                    val errorMessage =
                        exception.message
                            ?: exception::class.simpleName
                            ?: "Unknown import error"

                    errors.add(
                        "Excel row ${rowIndex + 1}, " +
                                "user ID ${userId ?: "missing"}: " +
                                errorMessage
                    )

                    println(
                        "Failed to import Excel row " +
                                "${rowIndex + 1}: $errorMessage"
                    )
                }
            }
        }

        return ImportResult(
            totalRows = totalRows,
            importedRows = importedRows,
            skippedRows = skippedRows,
            errors = errors
        )
    }

    private suspend fun importRow(
        row: Row,
        headers: Map<String, Int>
    ): Boolean {

        fun text(columnName: String): String? {
            return cleanNullableValue(
                getCell(
                    row = row,
                    headers = headers,
                    columnName = columnName
                )
            )
        }

        val userId = text("user_id")

        if (userId.isNullOrBlank()) {
            println(
                "Skipping Excel row ${row.rowNum + 1}: user_id is missing."
            )

            return false
        }

        // Basic text values

        val email = text("email")
        val fullName = text("full_name")

        val firstName = fullName

        val lastName: String? = null
        val phoneNumber = text("phone_number")
        val ghanaCardNumber = text("ghana_card_number")
        val socialSecurityNumber = text("social_security_number")

        val nationalHealthInsuranceNumber =
            text("national_health_insurance_number")

        val bankName = text("bank_name")
        val bankAccountBranch = text("bank_account_branch")
        val bankAccountNumber = text("bank_account_number")

        val professionalQualification =
            text("professional_qualification")

        val supervisorName = text("supervisor_name")

        val selfAssessmentDescription =
            text("self_assessment_description")

        // Profile picture values

        val profilePicture = text("profile_picture")

        val profilePictureUrl =
            ProfilePictureUtils.extractUrl(
                profilePicture
            )

        val profilePicturePublicId =
            ProfilePictureUtils.extractPublicId(
                profilePictureUrl
            )

        // Lookup-table names from Excel

        val regionName = text("region")
        val districtName = text("district")
        val directorateName = text("directorate")
        val categoryName = text("category")
        val currentGradeName = text("current_grade")
        val nextGradeName = text("next_grade")
        val changeOfGradeName = text("change_of_grade")

        val academicQualificationName =
            text("academic_qualifications")

        val managementUnitName =
            text("management_unit_cost_centre")

        val titleName = text("title")

        // Enum values

        val role =
            EnumMapper.role(
                text("role")
            )

        val professional =
            EnumMapper.professional(
                text("professional")
            )

        val gender =
            EnumMapper.gender(
                text("gender")
            )

        val maritalStatus =
            EnumMapper.maritalStatus(
                text("marital_status")
            )

        val payrollStatus =
            EnumMapper.payrollStatus(
                text("payroll_status")
            )

        val accommodationStatus =
            EnumMapper.accommodationStatus(
                text("accommodation_status")
            )

        val atPostOnLeave =
            EnumMapper.atPostOnLeave(
                text("at_post_on_leave")
            )

        val fulltimeContractStaff =
            EnumMapper.fulltimeContractStaff(
                text("fulltime_contract_staff")
            )

        val staffCategory =
            EnumMapper.staffCategory(
                text("staff_category")
            )

        val currentSalaryLevel =
            EnumMapper.salaryLevel(
                text("current_salary_level")
            )

        val nextSalaryLevel =
            EnumMapper.salaryLevel(
                text("next_salary_level")
            )

        val currentSalaryPoint =
            EnumMapper.salaryPoint(
                text("current_salary_point")
            )

        // Date values

        val dateOfBirth =
            getDateCell(
                row = row,
                headers = headers,
                columnName = "date_of_birth"
            )

        val dateOfAssumptionOfDuty =
            getDateCell(
                row = row,
                headers = headers,
                columnName = "date_of_assumption_of_duty"
            )

        val dateOfFirstAppointment =
            getDateCell(
                row = row,
                headers = headers,
                columnName = "date_of_first_appointment"
            )

        val dateOfLastPromotion =
            getDateCell(
                row = row,
                headers = headers,
                columnName = "date_of_last_promotion"
            )

        val nationalEffectiveDate =
            getDateCell(
                row = row,
                headers = headers,
                columnName = "national_effective_date"
            )

        val substantiveDate =
            getDateCell(
                row = row,
                headers = headers,
                columnName = "substantive_date"
            )

        // Salary and decimal values

        val singleSpineMonthlySalary =
            getBigDecimalCell(
                row = row,
                headers = headers,
                columnName = "single_spine_monthly_salary"
            )

        val monthlyGrossPay =
            getBigDecimalCell(
                row = row,
                headers = headers,
                columnName = "monthly_gross_pay"
            )

        val annualSalary =
            getBigDecimalCell(
                row = row,
                headers = headers,
                columnName = "annual_salary"
            )

        val overallAssessmentScore =
            getBigDecimalCell(
                row = row,
                headers = headers,
                columnName = "overall_assessment_score"
            )

        // Integer performance values

        val numberOfFocusAreas =
            getIntCell(
                row = row,
                headers = headers,
                columnName = "number_of_focus_areas"
            )

        val numberOfTargets =
            getIntCell(
                row = row,
                headers = headers,
                columnName = "number_of_targets"
            )

        val numberOfTargetsMet =
            getIntCell(
                row = row,
                headers = headers,
                columnName = "number_of_targets_met"
            )

        val numberOfTargetsNotMet =
            getIntCell(
                row = row,
                headers = headers,
                columnName = "number_of_targets_not_met"
            )

        return dbQuery {

            val accountAlreadyExists =
                Accounts
                    .selectAll()
                    .any { resultRow ->
                        resultRow[Accounts.userId]
                            ?.trim()
                            ?.equals(
                                other = userId,
                                ignoreCase = true
                            ) == true
                    }

            if (accountAlreadyExists) {
                println(
                    "Skipping existing account: $userId"
                )

                return@dbQuery false
            }

            // Foreign-key lookups

            val regionId =
                LookupService.findRegionId(
                    regionName
                )

            val districtId =
                LookupService.findDistrictId(
                    districtName
                )

            val directorateId =
                LookupService.findDirectorateId(
                    directorateName
                )

            val categoryId =
                LookupService.findCategoryId(
                    categoryName
                )

            val currentGradeId =
                LookupService.findCurrentGradeId(
                    currentGradeName
                )

            val nextGradeId =
                LookupService.findNextGradeId(
                    nextGradeName
                )

            val changeOfGradeId =
                LookupService.findChangeOfGradeId(
                    changeOfGradeName
                )

            val academicQualificationId =
                LookupService.findAcademicQualificationId(
                    academicQualificationName
                )

            val managementUnitId =
                LookupService.findManagementUnitId(
                    managementUnitName
                )

            val titleId =
                LookupService.findTitleId(
                    titleName
                )

            val passwordHash =
                BCrypt.hashpw(
                    DEFAULT_PASSWORD,
                    BCrypt.gensalt(12)
                )

            Accounts.insert { statement ->

                // Authentication fields

                statement[Accounts.userId] =
                    userId.take(222)

                statement[Accounts.passwordHash] =
                    passwordHash

                statement[Accounts.isActive] = true
                statement[Accounts.isStaff] = true
                statement[Accounts.isSuperuser] = false

                // Basic account information

                statement[Accounts.email] =
                    email?.take(30)

                statement[Accounts.firstName] =
                    firstName?.take(222)

                statement[Accounts.lastName] =
                    lastName?.take(222)

                statement[Accounts.phoneNumber] =
                    phoneNumber?.take(222)

                statement[Accounts.ghanaCardNumber] =
                    ghanaCardNumber?.take(222)

                statement[Accounts.socialSecurityNumber] =
                    socialSecurityNumber?.take(222)

                statement[
                    Accounts.nationalHealthInsuranceNumber
                ] = nationalHealthInsuranceNumber?.take(222)

                // Banking information

                statement[Accounts.bankName] =
                    bankName?.take(222)

                statement[Accounts.bankAccountBranch] =
                    bankAccountBranch?.take(100)

                statement[Accounts.bankAccountNumber] =
                    bankAccountNumber?.take(222)

                // Professional information

                statement[
                    Accounts.professionalQualification
                ] = professionalQualification?.take(100)

                statement[Accounts.supervisorName] =
                    supervisorName?.take(100)

                // Profile picture information

                statement[Accounts.profilePictureUrl] =
                    profilePictureUrl?.take(500)

                statement[
                    Accounts.profilePicturePublicId
                ] = profilePicturePublicId?.take(255)

                // Date fields

                if (dateOfBirth != null) {
                    statement[Accounts.dateOfBirth] =
                        dateOfBirth
                }

                if (dateOfAssumptionOfDuty != null) {
                    statement[
                        Accounts.dateOfAssumptionOfDuty
                    ] = dateOfAssumptionOfDuty
                }

                if (dateOfFirstAppointment != null) {
                    statement[
                        Accounts.dateOfFirstAppointment
                    ] = dateOfFirstAppointment
                }

                if (dateOfLastPromotion != null) {
                    statement[
                        Accounts.dateOfLastPromotion
                    ] = dateOfLastPromotion
                }

                if (nationalEffectiveDate != null) {
                    statement[
                        Accounts.nationalEffectiveDate
                    ] = nationalEffectiveDate
                }

                if (substantiveDate != null) {
                    statement[Accounts.substantiveDate] =
                        substantiveDate
                }

                // Salary fields

                if (singleSpineMonthlySalary != null) {
                    statement[
                        Accounts.singleSpineMonthlySalary
                    ] = singleSpineMonthlySalary
                }

                if (monthlyGrossPay != null) {
                    statement[Accounts.monthlyGrossPay] =
                        monthlyGrossPay
                }

                if (annualSalary != null) {
                    statement[Accounts.annualSalary] =
                        annualSalary
                }

                // Performance fields

                if (numberOfFocusAreas != null) {
                    statement[
                        Accounts.numberOfFocusAreas
                    ] = numberOfFocusAreas
                }

                if (numberOfTargets != null) {
                    statement[Accounts.numberOfTargets] =
                        numberOfTargets
                }

                if (numberOfTargetsMet != null) {
                    statement[
                        Accounts.numberOfTargetsMet
                    ] = numberOfTargetsMet
                }

                if (numberOfTargetsNotMet != null) {
                    statement[
                        Accounts.numberOfTargetsNotMet
                    ] = numberOfTargetsNotMet
                }

                if (overallAssessmentScore != null) {
                    statement[
                        Accounts.overallAssessmentScore
                    ] = overallAssessmentScore
                }

                statement[
                    Accounts.selfAssessmentDescription
                ] = selfAssessmentDescription

                // Enum fields

                if (role != null) {
                    statement[Accounts.role] = role
                }

                if (professional != null) {
                    statement[Accounts.professional] =
                        professional
                }

                if (gender != null) {
                    statement[Accounts.gender] = gender
                }

                if (maritalStatus != null) {
                    statement[Accounts.maritalStatus] =
                        maritalStatus
                }

                if (payrollStatus != null) {
                    statement[Accounts.payrollStatus] =
                        payrollStatus
                }

                if (accommodationStatus != null) {
                    statement[
                        Accounts.accommodationStatus
                    ] = accommodationStatus
                }

                if (atPostOnLeave != null) {
                    statement[Accounts.atPostOnLeave] =
                        atPostOnLeave
                }

                if (fulltimeContractStaff != null) {
                    statement[
                        Accounts.fulltimeContractStaff
                    ] = fulltimeContractStaff
                }

                if (staffCategory != null) {
                    statement[Accounts.staffCategory] =
                        staffCategory
                }

                if (currentSalaryLevel != null) {
                    statement[
                        Accounts.currentSalaryLevel
                    ] = currentSalaryLevel
                }

                if (nextSalaryLevel != null) {
                    statement[
                        Accounts.nextSalaryLevel
                    ] = nextSalaryLevel
                }

                if (currentSalaryPoint != null) {
                    statement[
                        Accounts.currentSalaryPoint
                    ] = currentSalaryPoint
                }

                // Foreign-key fields

                if (regionId != null) {
                    statement[Accounts.regionId] =
                        EntityID(
                            regionId,
                            Regions
                        )
                }

                if (districtId != null) {
                    statement[Accounts.districtId] =
                        EntityID(
                            districtId,
                            Districts
                        )
                }

                if (directorateId != null) {
                    statement[Accounts.directorateId] =
                        EntityID(
                            directorateId,
                            Departments
                        )
                }

                if (categoryId != null) {
                    statement[Accounts.categoryId] =
                        EntityID(
                            categoryId,
                            Classes
                        )
                }

                if (currentGradeId != null) {
                    statement[Accounts.currentGradeId] =
                        EntityID(
                            currentGradeId,
                            CurrentGrades
                        )
                }

                if (nextGradeId != null) {
                    statement[Accounts.nextGradeId] =
                        EntityID(
                            nextGradeId,
                            NextGrades
                        )
                }

                if (changeOfGradeId != null) {
                    statement[Accounts.changeOfGradeId] =
                        EntityID(
                            changeOfGradeId,
                            ChangeOfGrades
                        )
                }

                if (academicQualificationId != null) {
                    statement[
                        Accounts.academicQualificationId
                    ] = EntityID(
                        academicQualificationId,
                        AcademicQualifications
                    )
                }

                if (managementUnitId != null) {
                    statement[
                        Accounts.managementUnitCostCentreId
                    ] = EntityID(
                        managementUnitId,
                        ManagementUnits
                    )
                }

                if (titleId != null) {
                    statement[Accounts.titleId] =
                        EntityID(
                            titleId,
                            Titles
                        )
                }
            }

            println(
                "Imported account successfully: $userId"
            )

            true
        }
    }

    /*
     * All helper functions below, including getCell(),
     * remain inside ExcelAccountImporter.kt.
     */

    private fun createHeaderMap(
        headerRow: Row
    ): Map<String, Int> {

        val headers =
            mutableMapOf<String, Int>()

        for (cell in headerRow) {

            val headerName =
                dataFormatter
                    .formatCellValue(cell)
                    .trim()
                    .lowercase()

            if (headerName.isNotBlank()) {
                headers[headerName] =
                    cell.columnIndex
            }
        }

        return headers
    }

    private fun validateRequiredHeaders(
        headers: Map<String, Int>
    ) {
        val requiredHeaders = listOf(
            "user_id",
            "email",
            "first_name",
            "last_name",
            "role",
            "professional",
            "current_grade",
            "next_grade",
            "change_of_grade",
            "date_of_assumption_of_duty",
            "profile_picture",
            "directorate",
            "category",
            "region",
            "academic_qualifications",
            "title",
            "gender",
            "date_of_birth",
            "marital_status",
            "current_salary_level",
            "current_salary_point",
            "date_of_first_appointment",
            "date_of_last_promotion",
            "professional_qualification",
            "monthly_gross_pay",
            "annual_salary",
            "phone_number",
            "ghana_card_number",
            "social_security_number",
            "national_health_insurance_number",
            "bank_name",
            "bank_account_branch",
            "bank_account_number",
            "next_salary_level",
            "payroll_status",
            "accommodation_status",
            "supervisor_name",
            "at_post_on_leave",
            "fulltime_contract_staff",
            "national_effective_date",
            "substantive_date",
            "staff_category",
            "single_spine_monthly_salary",
            "self_assessment_description",
            "overall_assessment_score",
            "number_of_targets",
            "number_of_targets_met",
            "number_of_targets_not_met",
            "number_of_focus_areas",
            "management_unit_cost_centre",
            "district"
        )

        val missingHeaders = requiredHeaders.filterNot { requiredHeader ->
            headers.containsKey(
                requiredHeader.trim().lowercase()
            )
        }

        if (missingHeaders.isNotEmpty()) {
            throw IllegalArgumentException(
                buildString {
                    append("The Excel file is missing required columns: ")
                    append(missingHeaders.joinToString(", "))
                }
            )
        }
    }

    private fun getCell(
        row: Row,
        headers: Map<String, Int>,
        columnName: String
    ): String? {
        val columnIndex = headers[
            columnName.trim().lowercase()
        ] ?: return null

        val cell = row.getCell(
            columnIndex,
            Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
        ) ?: return null

        val value = dataFormatter
            .formatCellValue(cell)
            .trim()

        return cleanNullableValue(value)
    }

    private fun getDateCell(
        row: Row,
        headers: Map<String, Int>,
        columnName: String
    ): LocalDate? {
        val cell = getRawCell(
            row = row,
            headers = headers,
            columnName = columnName
        ) ?: return null

        if (
            cell.cellType == CellType.NUMERIC &&
            DateUtil.isCellDateFormatted(cell)
        ) {
            return cell.localDateTimeCellValue.toLocalDate()
        }

        val rawValue = dataFormatter
            .formatCellValue(cell)
            .trim()

        val cleanedValue = cleanNullableValue(rawValue)
            ?: return null

        supportedDateFormats.forEach { formatter ->
            try {
                return LocalDate.parse(
                    cleanedValue,
                    formatter
                )
            } catch (_: DateTimeParseException) {
                // Try the next supported date format.
            }
        }

        throw IllegalArgumentException(
            "Invalid date '$cleanedValue' in Excel column " +
                    "'$columnName' on row ${row.rowNum + 1}."
        )
    }

    private fun getBigDecimalCell(
        row: Row,
        headers: Map<String, Int>,
        columnName: String
    ): BigDecimal? {
        val cell = getRawCell(
            row = row,
            headers = headers,
            columnName = columnName
        ) ?: return null

        if (cell.cellType == CellType.NUMERIC) {
            return BigDecimal
                .valueOf(cell.numericCellValue)
                .stripTrailingZeros()
        }

        val rawValue = dataFormatter
            .formatCellValue(cell)
            .trim()

        val cleanedValue = cleanNullableValue(rawValue)
            ?: return null

        val normalizedValue = cleanedValue
            .replace(",", "")
            .replace("GHS", "", ignoreCase = true)
            .replace("GH₵", "", ignoreCase = true)
            .replace("GH¢", "", ignoreCase = true)
            .replace("₵", "")
            .replace("¢", "")
            .replace(" ", "")
            .trim()

        return normalizedValue.toBigDecimalOrNull()
            ?: throw IllegalArgumentException(
                "Invalid decimal '$cleanedValue' in Excel column " +
                        "'$columnName' on row ${row.rowNum + 1}."
            )
    }

    private fun getIntCell(
        row: Row,
        headers: Map<String, Int>,
        columnName: String
    ): Int? {
        val cell = getRawCell(
            row = row,
            headers = headers,
            columnName = columnName
        ) ?: return null

        if (cell.cellType == CellType.NUMERIC) {
            val numericValue = cell.numericCellValue

            if (
                numericValue.isNaN() ||
                numericValue.isInfinite()
            ) {
                throw IllegalArgumentException(
                    "Invalid numeric value in Excel column " +
                            "'$columnName' on row ${row.rowNum + 1}."
                )
            }

            return numericValue.toInt()
        }

        val rawValue = dataFormatter
            .formatCellValue(cell)
            .trim()

        val cleanedValue = cleanNullableValue(rawValue)
            ?: return null

        val normalizedValue = cleanedValue
            .replace(",", "")
            .trim()

        return normalizedValue.toIntOrNull()
            ?: normalizedValue.toDoubleOrNull()?.toInt()
            ?: throw IllegalArgumentException(
                "Invalid integer '$cleanedValue' in Excel column " +
                        "'$columnName' on row ${row.rowNum + 1}."
            )
    }

    private fun getRawCell(
        row: Row,
        headers: Map<String, Int>,
        columnName: String
    ): Cell? {
        val columnIndex = headers[
            columnName.trim().lowercase()
        ] ?: return null

        return row.getCell(
            columnIndex,
            Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
        )
    }

    private fun cleanNullableValue(
        value: String?
    ): String? {
        val cleanedValue = value?.trim()

        if (cleanedValue.isNullOrBlank()) {
            return null
        }

        return when (cleanedValue.uppercase()) {
            "N/A",
            "NA",
            "NIL",
            "NONE",
            "NULL",
            "NOT APPLICABLE",
            "-",
            "--" -> null

            else -> cleanedValue
        }
    }

    private fun isRowEmpty(
        row: Row
    ): Boolean {
        val firstColumnIndex = row.firstCellNum
            .toInt()
            .coerceAtLeast(0)

        val lastColumnIndex = row.lastCellNum.toInt()

        if (lastColumnIndex <= firstColumnIndex) {
            return true
        }

        for (
        columnIndex in firstColumnIndex until lastColumnIndex
        ) {
            val cell = row.getCell(
                columnIndex,
                Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
            )

            if (cell != null) {
                val value = dataFormatter
                    .formatCellValue(cell)
                    .trim()

                if (value.isNotBlank()) {
                    return false
                }
            }
        }

        return true
    }}