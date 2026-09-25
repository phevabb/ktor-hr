package com.hr.staff.services

import com.hr.auth.models.AuthPrincipal
import com.hr.auth.repositories.AuthRepository
import com.hr.staff.dtos.StaffSummaryResponse
import com.hr.staff.repositories.StaffRepository
import kotlin.collections.emptyList


object StaffService {

    suspend fun getAll(
        principal: AuthPrincipal
    ): List<StaffSummaryResponse> {
        return when {
            principal.role.equals(
                other = "Admin",
                ignoreCase = true
            ) -> {
                StaffRepository.getAll()
            }

            principal.role.equals(
                other = "Manager",
                ignoreCase = true
            ) -> {
                val managerRegion =
                    AuthRepository.findManagerRegion(
                        principal.accountId
                    )
                        ?: return emptyList()

                StaffRepository.getByRegionId(
                    managerRegion.regionId
                )
            }

            principal.role.equals(
                other = "Staff",
                ignoreCase = true
            ) -> {
                StaffRepository.getSummaryById(
                    principal.accountId
                )
                    ?.let {
                        listOf(it)
                    }
                    ?: emptyList()
            }

            else -> {
                emptyList()
            }
        }
    }

    suspend fun getById(
        principal: AuthPrincipal,
        staffId: Int
    ): StaffResult {
        val requestedStaff =
            StaffRepository.getById(
                staffId
            )
                ?: return StaffResult.NotFound

        if (
            principal.role.equals(
                other = "Staff",
                ignoreCase = true
            ) &&
            principal.accountId != staffId
        ) {
            return StaffResult.AccessDenied
        }

        if (
            principal.role.equals(
                other = "Manager",
                ignoreCase = true
            )
        ) {
            val managerRegion =
                AuthRepository.findManagerRegion(
                    principal.accountId
                )
                    ?: return StaffResult
                        .ManagerRegionMissing

            if (
                requestedStaff.regionId !=
                managerRegion.regionId
            ) {
                return StaffResult.AccessDenied
            }
        }

        return StaffResult.Success(
            requestedStaff
        )
    }
}