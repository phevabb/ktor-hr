package com.hr.admin.services

import com.hr.admin.dtos.ManagementUnitStatsPageResponse
import com.hr.admin.repositories.ManagementUnitStatsRepository
import com.hr.auth.repositories.AuthRepository
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

object ManagementUnitStatsService {

    suspend fun getManagementUnitStats(
        accountId: Int,
        page: Int,
        pageSize: Int,
        regionName: String?,
        requestPath: String
    ): ManagementUnitStatsResult {
        val account =
            AuthRepository
                .findAccountById(
                    accountId
                )
                ?: return ManagementUnitStatsResult
                    .AccountNotFound

        if (!account.isActive) {
            return ManagementUnitStatsResult
                .AccountInactive
        }

        val isAdmin =
            account.role.equals(
                other = "Admin",
                ignoreCase = true
            )

        if (!isAdmin) {
            println(
                "Management unit statistics access denied: " +
                        "accountId=${account.id}, " +
                        "userId=${account.userId}, " +
                        "role=${account.role}"
            )

            return ManagementUnitStatsResult
                .AccessDenied
        }

        return try {
            val allStatistics =
                ManagementUnitStatsRepository
                    .getAll(
                        regionName =
                            regionName
                    )

            val safePageSize =
                pageSize.coerceIn(
                    minimumValue = 1,
                    maximumValue = 100
                )

            val totalRecords =
                allStatistics.size

            val totalPages =
                max(
                    1,
                    ceil(
                        totalRecords.toDouble() /
                                safePageSize.toDouble()
                    ).toInt()
                )

            val safePage =
                page.coerceIn(
                    minimumValue = 1,
                    maximumValue = totalPages
                )

            val startIndex =
                (
                        (safePage - 1) *
                                safePageSize
                        ).coerceAtMost(
                        totalRecords
                    )

            val endIndex =
                min(
                    startIndex +
                            safePageSize,
                    totalRecords
                )

            val pageResults =
                if (
                    startIndex <
                    endIndex
                ) {
                    allStatistics.subList(
                        startIndex,
                        endIndex
                    )
                } else {
                    emptyList()
                }

            val nextPageUrl =
                if (
                    safePage <
                    totalPages
                ) {
                    buildPageUrl(
                        requestPath =
                            requestPath,

                        page =
                            safePage + 1,

                        pageSize =
                            safePageSize,

                        regionName =
                            regionName
                    )
                } else {
                    null
                }

            val previousPageUrl =
                if (safePage > 1) {
                    buildPageUrl(
                        requestPath =
                            requestPath,

                        page =
                            safePage - 1,

                        pageSize =
                            safePageSize,

                        regionName =
                            regionName
                    )
                } else {
                    null
                }

            val response =
                ManagementUnitStatsPageResponse(
                    count =
                        totalRecords,

                    next =
                        nextPageUrl,

                    previous =
                        previousPageUrl,

                    results =
                        pageResults
                )

            println(
                "Management unit statistics returned: " +
                        "accountId=${account.id}, " +
                        "region=${regionName ?: "All regions"}, " +
                        "page=$safePage, " +
                        "pageSize=$safePageSize, " +
                        "count=${response.count}, " +
                        "results=${response.results}"
            )

            ManagementUnitStatsResult.Success(
                statistics =
                    response
            )
        } catch (exception: Exception) {
            println(
                "Management unit statistics failed: " +
                        "accountId=${account.id}, " +
                        "errorType=${exception::class.simpleName}, " +
                        "message=${exception.message}"
            )

            ManagementUnitStatsResult.Failed
        }
    }

    private fun buildPageUrl(
        requestPath: String,
        page: Int,
        pageSize: Int,
        regionName: String?
    ): String {
        val baseUrl =
            (
                    "$requestPath" +
                            "?page=$page" +
                            "&page_size=$pageSize"
                    )

        val normalizedRegionName =
            regionName
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }

        if (normalizedRegionName == null) {
            return baseUrl
        }

        val encodedRegionName =
            URLEncoder.encode(
                normalizedRegionName,
                StandardCharsets.UTF_8
                    .toString()
            )

        return (
                "$baseUrl" +
                        "&region=$encodedRegionName"
                )
    }
}
