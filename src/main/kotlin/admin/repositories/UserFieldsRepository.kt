package com.hr.admin.repositories

import com.hr.academicqualification.table.AcademicQualifications
import com.hr.changeofgrade.tables.ChangeOfGrades
import com.hr.classes.tables.Classes
import com.hr.currentgrade.tables.CurrentGrades
import com.hr.department.table.Departments
import com.hr.districts.tables.Districts
import com.hr.managementUnits.tables.ManagementUnits
import com.hr.nextgrade.tables.NextGrades
import com.hr.onleavetype.table.OnLeaveTypes
import com.hr.region.tables.Regions
import com.hr.title.table.Titles
import com.hr.admin.dtos.UserFieldItemResponse
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.r2dbc.selectAll

object UserFieldsRepository {

    suspend fun getAcademicQualifications():
            List<UserFieldItemResponse> {
        return AcademicQualifications
            .selectAll()
            .orderBy(
                AcademicQualifications.name,
                SortOrder.ASC
            )
            .map { row ->
                UserFieldItemResponse(
                    id =
                        row[
                            AcademicQualifications.id
                        ].value,

                    name =
                        row[
                            AcademicQualifications.name
                        ]
                )
            }
            .toList()
    }

    suspend fun getDepartments():
            List<UserFieldItemResponse> {
        return Departments
            .selectAll()
            .orderBy(
                Departments.departmentName,
                SortOrder.ASC
            )
            .map { row ->
                UserFieldItemResponse(
                    id =
                        row[Departments.id]
                            .value,

                    name =
                        row[
                            Departments.departmentName
                        ]
                )
            }
            .toList()
    }

    suspend fun getClasses():
            List<UserFieldItemResponse> {
        return Classes
            .selectAll()
            .orderBy(
                Classes.classesName,
                SortOrder.ASC
            )
            .map { row ->
                UserFieldItemResponse(
                    id =
                        row[Classes.id]
                            .value,

                    name =
                        row[
                            Classes.classesName
                        ]
                )
            }
            .toList()
    }

    suspend fun getDistricts():
            List<UserFieldItemResponse> {
        return Districts
            .selectAll()
            .orderBy(
                Districts.district,
                SortOrder.ASC
            )
            .map { row ->
                UserFieldItemResponse(
                    id =
                        row[Districts.id]
                            .value,

                    name =
                        row[
                            Districts.district
                        ]
                )
            }
            .toList()
    }

    suspend fun getRegions():
            List<UserFieldItemResponse> {
        return Regions
            .selectAll()
            .orderBy(
                Regions.region,
                SortOrder.ASC
            )
            .map { row ->
                UserFieldItemResponse(
                    id =
                        row[Regions.id]
                            .value,

                    name =
                        row[Regions.region]
                )
            }
            .toList()
    }

    suspend fun getCurrentGrades():
            List<UserFieldItemResponse> {
        return CurrentGrades
            .selectAll()
            .orderBy(
                CurrentGrades.currentGrade,
                SortOrder.ASC
            )
            .map { row ->
                UserFieldItemResponse(
                    id =
                        row[CurrentGrades.id]
                            .value,

                    name =
                        row[
                            CurrentGrades.currentGrade
                        ]
                )
            }
            .toList()
    }

    suspend fun getNextGrades():
            List<UserFieldItemResponse> {
        return NextGrades
            .selectAll()
            .orderBy(
                NextGrades.nextGrade,
                SortOrder.ASC
            )
            .map { row ->
                UserFieldItemResponse(
                    id =
                        row[NextGrades.id]
                            .value,

                    name =
                        row[
                            NextGrades.nextGrade
                        ]
                )
            }
            .toList()
    }

    suspend fun getChangeOfGrades():
            List<UserFieldItemResponse> {
        return ChangeOfGrades
            .selectAll()
            .orderBy(
                ChangeOfGrades.grade,
                SortOrder.ASC
            )
            .map { row ->
                UserFieldItemResponse(
                    id =
                        row[ChangeOfGrades.id]
                            .value,

                    name =
                        row[
                            ChangeOfGrades.grade
                        ]
                )
            }
            .toList()
    }

    suspend fun getManagementUnits():
            List<UserFieldItemResponse> {
        return ManagementUnits
            .selectAll()
            .orderBy(
                ManagementUnits.managementUnitName,
                SortOrder.ASC
            )
            .map { row ->
                UserFieldItemResponse(
                    id =
                        row[ManagementUnits.id]
                            .value,

                    name =
                        row[
                            ManagementUnits
                                .managementUnitName
                        ]
                )
            }
            .toList()
    }

    suspend fun getTitles():
            List<UserFieldItemResponse> {
        return Titles
            .selectAll()
            .orderBy(
                Titles.title,
                SortOrder.ASC
            )
            .map { row ->
                UserFieldItemResponse(
                    id =
                        row[Titles.id]
                            .value,

                    name =
                        row[Titles.title]
                )
            }
            .toList()
    }

    suspend fun getLeaveTypes():
            List<UserFieldItemResponse> {
        return OnLeaveTypes
            .selectAll()
            .orderBy(
                OnLeaveTypes.name,
                SortOrder.ASC
            )
            .map { row ->
                UserFieldItemResponse(
                    id =
                        row[OnLeaveTypes.id]
                            .value,

                    name =
                        row[OnLeaveTypes.name]
                )
            }
            .toList()
    }
}
