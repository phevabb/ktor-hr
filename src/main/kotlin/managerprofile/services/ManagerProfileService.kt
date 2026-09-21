package com.hr.managerprofile.services

import com.hr.managerprofile.dtos.ManagerProfileOperationResult
import com.hr.managerprofile.dtos.ManagerProfileRequest
import com.hr.managerprofile.repositories.ManagerProfileRepository

object ManagerProfileService {

    suspend fun create(
        request: ManagerProfileRequest
    ): ManagerProfileOperationResult {
        val accountExists =
            ManagerProfileRepository
                .accountExists(
                    request.accountId
                )

        if (!accountExists) {
            return ManagerProfileOperationResult
                .AccountNotFound
        }

        val regionExists =
            ManagerProfileRepository
                .regionExists(
                    request.regionId
                )

        if (!regionExists) {
            return ManagerProfileOperationResult
                .RegionNotFound
        }

        val accountAlreadyAssigned =
            ManagerProfileRepository
                .accountAlreadyAssigned(
                    accountId =
                        request.accountId
                )

        if (accountAlreadyAssigned) {
            return ManagerProfileOperationResult
                .AccountAlreadyAssigned
        }

        val managerProfileId =
            ManagerProfileRepository
                .create(
                    accountId =
                        request.accountId,
                    regionId =
                        request.regionId
                )

        val managerProfile =
            ManagerProfileRepository
                .getById(
                    managerProfileId
                )
                ?: return ManagerProfileOperationResult
                    .Failed

        return ManagerProfileOperationResult
            .Success(
                managerProfile
            )
    }

    suspend fun update(
        id: Int,
        request: ManagerProfileRequest
    ): ManagerProfileOperationResult {
        val existingManagerProfile =
            ManagerProfileRepository
                .getById(id)
                ?: return ManagerProfileOperationResult
                    .ManagerProfileNotFound

        val accountExists =
            ManagerProfileRepository
                .accountExists(
                    request.accountId
                )

        if (!accountExists) {
            return ManagerProfileOperationResult
                .AccountNotFound
        }

        val regionExists =
            ManagerProfileRepository
                .regionExists(
                    request.regionId
                )

        if (!regionExists) {
            return ManagerProfileOperationResult
                .RegionNotFound
        }

        val accountAlreadyAssigned =
            ManagerProfileRepository
                .accountAlreadyAssigned(
                    accountId =
                        request.accountId,
                    excludeManagerProfileId =
                        existingManagerProfile.id
                )

        if (accountAlreadyAssigned) {
            return ManagerProfileOperationResult
                .AccountAlreadyAssigned
        }

        val updated =
            ManagerProfileRepository
                .update(
                    id = id,
                    accountId =
                        request.accountId,
                    regionId =
                        request.regionId
                )

        if (!updated) {
            return ManagerProfileOperationResult
                .Failed
        }

        val managerProfile =
            ManagerProfileRepository
                .getById(id)
                ?: return ManagerProfileOperationResult
                    .Failed

        return ManagerProfileOperationResult
            .Success(
                managerProfile
            )
    }
}
