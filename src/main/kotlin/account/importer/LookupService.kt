package com.hr.account.importer

import com.hr.academicqualification.table.AcademicQualifications
import com.hr.changeofgrade.tables.ChangeOfGrades
import com.hr.classes.tables.Classes
import com.hr.currentgrade.tables.CurrentGrades
import com.hr.department.table.Departments
import com.hr.districts.tables.Districts
import com.hr.managementUnits.tables.ManagementUnits
import com.hr.nextgrade.tables.NextGrades
import com.hr.region.tables.Regions
import com.hr.title.table.Titles
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.r2dbc.selectAll

object LookupService {

    suspend fun findRegionId(
        name: String?
    ): Int? {
        val cleanedName = cleanLookupValue(name)
            ?: return null

        return Regions
            .selectAll()
            .firstOrNull { resultRow ->
                normalizeLookupValue(
                    resultRow[Regions.region]
                ) == normalizeLookupValue(
                    cleanedName
                )
            }
            ?.get(Regions.id)
            ?.value
    }

    suspend fun findDistrictId(
        name: String?
    ): Int? {
        val cleanedName = cleanLookupValue(name)
            ?: return null

        return Districts
            .selectAll()
            .firstOrNull { resultRow ->
                normalizeLookupValue(
                    resultRow[Districts.district]
                ) == normalizeLookupValue(
                    cleanedName
                )
            }
            ?.get(Districts.id)
            ?.value
    }

    suspend fun findDirectorateId(
        name: String?
    ): Int? {
        val cleanedName = cleanLookupValue(name)
            ?: return null

        return Departments
            .selectAll()
            .firstOrNull { resultRow ->
                normalizeLookupValue(
                    resultRow[
                        Departments.departmentName
                    ]
                ) == normalizeLookupValue(
                    cleanedName
                )
            }
            ?.get(Departments.id)
            ?.value
    }

    suspend fun findCategoryId(
        name: String?
    ): Int? {
        val cleanedName = cleanLookupValue(name)
            ?: return null

        return Classes
            .selectAll()
            .firstOrNull { resultRow ->
                normalizeLookupValue(
                    resultRow[Classes.classesName]
                ) == normalizeLookupValue(
                    cleanedName
                )
            }
            ?.get(Classes.id)
            ?.value
    }

    suspend fun findCurrentGradeId(
        name: String?
    ): Int? {
        val cleanedName = cleanLookupValue(name)
            ?: return null

        return CurrentGrades
            .selectAll()
            .firstOrNull { resultRow ->
                normalizeLookupValue(
                    resultRow[
                        CurrentGrades.currentGrade
                    ]
                ) == normalizeLookupValue(
                    cleanedName
                )
            }
            ?.get(CurrentGrades.id)
            ?.value
    }

    suspend fun findNextGradeId(
        name: String?
    ): Int? {
        val cleanedName = cleanLookupValue(name)
            ?: return null

        return NextGrades
            .selectAll()
            .firstOrNull { resultRow ->
                normalizeLookupValue(
                    resultRow[NextGrades.nextGrade]
                ) == normalizeLookupValue(
                    cleanedName
                )
            }
            ?.get(NextGrades.id)
            ?.value
    }

    suspend fun findChangeOfGradeId(
        name: String?
    ): Int? {
        val cleanedName = cleanLookupValue(name)
            ?: return null

        return ChangeOfGrades
            .selectAll()
            .firstOrNull { resultRow ->
                normalizeLookupValue(
                    resultRow[ChangeOfGrades.grade]
                ) == normalizeLookupValue(
                    cleanedName
                )
            }
            ?.get(ChangeOfGrades.id)
            ?.value
    }

    suspend fun findAcademicQualificationId(
        name: String?
    ): Int? {
        val cleanedName = cleanLookupValue(name)
            ?: return null

        return AcademicQualifications
            .selectAll()
            .firstOrNull { resultRow ->
                normalizeLookupValue(
                    resultRow[
                        AcademicQualifications.name
                    ]
                ) == normalizeLookupValue(
                    cleanedName
                )
            }
            ?.get(AcademicQualifications.id)
            ?.value
    }

    suspend fun findManagementUnitId(
        name: String?
    ): Int? {
        val cleanedName = cleanLookupValue(name)
            ?: return null

        return ManagementUnits
            .selectAll()
            .firstOrNull { resultRow ->
                normalizeLookupValue(
                    resultRow[
                        ManagementUnits.managementUnitName
                    ]
                ) == normalizeLookupValue(
                    cleanedName
                )
            }
            ?.get(ManagementUnits.id)
            ?.value
    }

    suspend fun findTitleId(
        name: String?
    ): Int? {
        val cleanedName = cleanLookupValue(name)
            ?: return null

        return Titles
            .selectAll()
            .firstOrNull { resultRow ->
                normalizeLookupValue(
                    resultRow[Titles.title]
                ) == normalizeLookupValue(
                    cleanedName
                )
            }
            ?.get(Titles.id)
            ?.value
    }

    private fun cleanLookupValue(
        value: String?
    ): String? {
        val cleanedValue = value?.trim()

        if (cleanedValue.isNullOrBlank()) {
            return null
        }

        return when (
            cleanedValue.uppercase()
        ) {
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

    private fun normalizeLookupValue(
        value: String
    ): String {
        return value
            .trim()
            .lowercase()
            .replace("&", "and")
            .replace("-", " ")
            .replace("_", " ")
            .replace(
                Regex("\\s+"),
                " "
            )
    }
}