package com.hr.manager.repositories

import com.hr.academicqualification.table.AcademicQualifications
import com.hr.changeofgrade.tables.ChangeOfGrades
import com.hr.currentgrade.tables.CurrentGrades

import com.hr.department.table.Departments
import com.hr.districts.tables.Districts
import com.hr.managementUnits.tables.ManagementUnits


import com.hr.manager.dtos.ManagerUserFieldItemResponse
import com.hr.nextgrade.tables.NextGrades

import com.hr.onleavetype.table.OnLeaveTypes
import com.hr.staffclass.table.StaffClasses
import com.hr.title.table.Titles
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagerUserFieldsRepository {

    suspend fun getAcademicQualifications():
            List<ManagerUserFieldItemResponse> {
        println(
            "Retrieving academic qualifications for Manager user fields"
        )

        return AcademicQualifications
            .selectAll()
            .orderBy(
                AcademicQualifications.name,
                SortOrder.ASC
            )
            .map { row ->
                ManagerUserFieldItemResponse(
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

    suspend fun getDirectorates():
            List<ManagerUserFieldItemResponse> {
        println(
            "Retrieving directorates for Manager user fields"
        )

        return Departments
            .selectAll()
            .orderBy(
                Departments.departmentName,
                SortOrder.ASC
            )
            .map { row ->
                ManagerUserFieldItemResponse(
                    id =
                        row[
                            Departments.id
                        ].value,

                    name =
                        row[
                            Departments.departmentName
                        ]
                )
            }
            .toList()
    }

    suspend fun getStaffClasses():
            List<ManagerUserFieldItemResponse> {
        println(
            "Retrieving staff classes for Manager user fields"
        )

        return StaffClasses
            .selectAll()
            .orderBy(
                StaffClasses.name,
                SortOrder.ASC
            )
            .map { row ->
                ManagerUserFieldItemResponse(
                    id =
                        row[
                            StaffClasses.id
                        ].value,

                    name =
                        row[
                            StaffClasses.name
                        ]
                )
            }
            .toList()
    }

    suspend fun getDistricts():
            List<ManagerUserFieldItemResponse> {
        println(
            "Retrieving districts for Manager user fields"
        )

        return Districts
            .selectAll()
            .orderBy(
                Districts.district,
                SortOrder.ASC
            )
            .map { row ->
                ManagerUserFieldItemResponse(
                    id =
                        row[
                            Districts.id
                        ].value,

                    name =
                        row[
                            Districts.district
                        ]
                )
            }
            .toList()
    }

    suspend fun getCurrentGrades():
            List<ManagerUserFieldItemResponse> {
        println(
            "Retrieving current grades for Manager user fields"
        )

        return CurrentGrades
            .selectAll()
            .orderBy(
                CurrentGrades.currentGrade,
                SortOrder.ASC
            )
            .map { row ->
                ManagerUserFieldItemResponse(
                    id =
                        row[
                            CurrentGrades.id
                        ].value,

                    name =
                        row[
                            CurrentGrades.currentGrade
                        ]
                )
            }
            .toList()
    }

    suspend fun getNextGrades():
            List<ManagerUserFieldItemResponse> {
        println(
            "Retrieving next grades for Manager user fields"
        )

        return NextGrades
            .selectAll()
            .orderBy(
                NextGrades.nextGrade,
                SortOrder.ASC
            )
            .map { row ->
                ManagerUserFieldItemResponse(
                    id =
                        row[
                            NextGrades.id
                        ].value,

                    name =
                        row[
                            NextGrades.nextGrade
                        ]
                )
            }
            .toList()
    }

    suspend fun getChangeOfGrades():
            List<ManagerUserFieldItemResponse> {
        println(
            "Retrieving change-of-grade values for Manager user fields"
        )

        return ChangeOfGrades
            .selectAll()
            .orderBy(
                ChangeOfGrades.grade,
                SortOrder.ASC
            )
            .map { row ->
                ManagerUserFieldItemResponse(
                    id =
                        row[
                            ChangeOfGrades.id
                        ].value,

                    name =
                        row[
                            ChangeOfGrades.grade
                        ]
                )
            }
            .toList()
    }

    suspend fun getManagementUnits():
            List<ManagerUserFieldItemResponse> {
        println(
            "Retrieving management units for Manager user fields"
        )

        return ManagementUnits
            .selectAll()
            .orderBy(
                ManagementUnits.managementUnitName,
                SortOrder.ASC
            )
            .map { row ->
                ManagerUserFieldItemResponse(
                    id =
                        row[
                            ManagementUnits.id
                        ].value,

                    name =
                        row[
                            ManagementUnits.managementUnitName
                        ]
                )
            }
            .toList()
    }

    suspend fun getTitles():
            List<ManagerUserFieldItemResponse> {
        println(
            "Retrieving titles for Manager user fields"
        )

        return Titles
            .selectAll()
            .orderBy(
                Titles.title,
                SortOrder.ASC
            )
            .map { row ->
                ManagerUserFieldItemResponse(
                    id =
                        row[
                            Titles.id
                        ].value,

                    name =
                        row[
                            Titles.title
                        ]
                )
            }
            .toList()
    }

    suspend fun getLeaveTypes():
            List<ManagerUserFieldItemResponse> {
        println(
            "Retrieving leave types for Manager user fields"
        )

        return OnLeaveTypes
            .selectAll()
            .orderBy(
                OnLeaveTypes.name,
                SortOrder.ASC
            )
            .map { row ->
                ManagerUserFieldItemResponse(
                    id =
                        row[
                            OnLeaveTypes.id
                        ].value,

                    name =
                        row[
                            OnLeaveTypes.name
                        ]
                )
            }
            .toList()
    }
}