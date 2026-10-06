package com.hr.admin.repositories

import com.hr.admin.dtos.AdminUserFieldItemResponse

object AdminUserFieldsRepository {

    suspend fun getAcademicQualifications():
            List<AdminUserFieldItemResponse> {
        return UserFieldsRepository
            .getAcademicQualifications()
            .map { item ->
                AdminUserFieldItemResponse(
                    id =
                        item.id,

                    name =
                        item.name
                )
            }
    }

    suspend fun getDepartments():
            List<AdminUserFieldItemResponse> {
        return UserFieldsRepository
            .getDepartments()
            .map { item ->
                AdminUserFieldItemResponse(
                    id =
                        item.id,

                    name =
                        item.name
                )
            }
    }

    suspend fun getClasses():
            List<AdminUserFieldItemResponse> {
        return UserFieldsRepository
            .getClasses()
            .map { item ->
                AdminUserFieldItemResponse(
                    id =
                        item.id,

                    name =
                        item.name
                )
            }
    }

    suspend fun getDistricts():
            List<AdminUserFieldItemResponse> {
        return UserFieldsRepository
            .getDistricts()
            .map { item ->
                AdminUserFieldItemResponse(
                    id =
                        item.id,

                    name =
                        item.name
                )
            }
    }

    suspend fun getRegions():
            List<AdminUserFieldItemResponse> {
        return UserFieldsRepository
            .getRegions()
            .map { item ->
                AdminUserFieldItemResponse(
                    id =
                        item.id,

                    name =
                        item.name
                )
            }
    }

    suspend fun getCurrentGrades():
            List<AdminUserFieldItemResponse> {
        return UserFieldsRepository
            .getCurrentGrades()
            .map { item ->
                AdminUserFieldItemResponse(
                    id =
                        item.id,

                    name =
                        item.name
                )
            }
    }

    suspend fun getNextGrades():
            List<AdminUserFieldItemResponse> {
        return UserFieldsRepository
            .getNextGrades()
            .map { item ->
                AdminUserFieldItemResponse(
                    id =
                        item.id,

                    name =
                        item.name
                )
            }
    }

    suspend fun getChangeOfGrades():
            List<AdminUserFieldItemResponse> {
        return UserFieldsRepository
            .getChangeOfGrades()
            .map { item ->
                AdminUserFieldItemResponse(
                    id =
                        item.id,

                    name =
                        item.name
                )
            }
    }

    suspend fun getManagementUnits():
            List<AdminUserFieldItemResponse> {
        return UserFieldsRepository
            .getManagementUnits()
            .map { item ->
                AdminUserFieldItemResponse(
                    id =
                        item.id,

                    name =
                        item.name
                )
            }
    }

    suspend fun getTitles():
            List<AdminUserFieldItemResponse> {
        return UserFieldsRepository
            .getTitles()
            .map { item ->
                AdminUserFieldItemResponse(
                    id =
                        item.id,

                    name =
                        item.name
                )
            }
    }

    suspend fun getLeaveTypes():
            List<AdminUserFieldItemResponse> {
        return UserFieldsRepository
            .getLeaveTypes()
            .map { item ->
                AdminUserFieldItemResponse(
                    id =
                        item.id,

                    name =
                        item.name
                )
            }
    }
}