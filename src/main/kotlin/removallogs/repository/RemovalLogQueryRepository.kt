package com.hr.removallogs.repository

import com.hr.account.table.Accounts
import com.hr.removallogs.dto.RemovalLogResponse
import com.hr.removallogs.table.UserRemovalLogs
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.r2dbc.selectAll

private data class AccountIdentity(
    val id: Int,
    val userId: String?,
    val fullName: String
)

object RemovalLogQueryRepository {

    suspend fun countAll(): Long {
        println(
            "Counting removal logs"
        )

        val total =
            UserRemovalLogs
                .selectAll()
                .count()

        println(
            "Removal-log count: $total"
        )

        return total
    }

    suspend fun getPage(
        page: Int,
        pageSize: Int
    ): List<RemovalLogResponse> {
        val normalizedPage =
            page.coerceAtLeast(
                1
            )

        val normalizedPageSize =
            pageSize.coerceIn(
                minimumValue = 1,
                maximumValue = 100
            )

        val calculatedOffset =
            (
                    normalizedPage -
                            1
                    ).toLong() *
                    normalizedPageSize.toLong()

        println(
            "Retrieving removal-log page"
        )

        println(
            "Page: $normalizedPage"
        )

        println(
            "Page size: $normalizedPageSize"
        )

        println(
            "Offset: $calculatedOffset"
        )

        val removalLogRows =
            UserRemovalLogs
                .selectAll()
                .orderBy(
                    UserRemovalLogs.removedAt,
                    SortOrder.DESC
                )
                .orderBy(
                    UserRemovalLogs.id,
                    SortOrder.DESC
                )
                .limit(
                    normalizedPageSize
                )
                .offset(
                    calculatedOffset
                )
                .toList()

        println(
            "Removal-log rows retrieved: ${removalLogRows.size}"
        )

        if (removalLogRows.isEmpty()) {
            return emptyList()
        }

        val referencedAccountIds =
            removalLogRows
                .flatMap { row ->
                    listOf(
                        row[
                            UserRemovalLogs.accountId
                        ].value,

                        row[
                            UserRemovalLogs.removedByAccountId
                        ].value
                    )
                }
                .distinct()

        println(
            "Referenced account IDs: $referencedAccountIds"
        )

        val accountEntityIds =
            referencedAccountIds.map { accountId ->
                EntityID(
                    id = accountId,
                    table = Accounts
                )
            }

        val accountRows =
            Accounts
                .selectAll()
                .where {
                    Accounts.id inList
                            accountEntityIds
                }
                .toList()

        println(
            "Referenced accounts retrieved: ${accountRows.size}"
        )

        val accountIdentityById =
            accountRows.associate { row ->
                val accountId =
                    row[
                        Accounts.id
                    ].value

                val userId =
                    row[
                        Accounts.userId
                    ]

                val firstName =
                    row[
                        Accounts.firstName
                    ]

                val middleName =
                    row[
                        Accounts.middleName
                    ]

                val lastName =
                    row[
                        Accounts.lastName
                    ]

                val fullName =
                    buildFullName(
                        firstName = firstName,
                        middleName = middleName,
                        lastName = lastName,
                        userId = userId,
                        accountId = accountId
                    )

                accountId to
                        AccountIdentity(
                            id = accountId,
                            userId = userId,
                            fullName = fullName
                        )
            }

        return removalLogRows.map { row ->
            val removedAccountId =
                row[
                    UserRemovalLogs.accountId
                ].value

            val removerAccountId =
                row[
                    UserRemovalLogs.removedByAccountId
                ].value

            val removedAccount =
                accountIdentityById[
                    removedAccountId
                ]

            val removerAccount =
                accountIdentityById[
                    removerAccountId
                ]

            RemovalLogResponse(
                id =
                    row[
                        UserRemovalLogs.id
                    ].value,

                accountId =
                    removedAccountId,

                removedAccountFullName =
                    removedAccount
                        ?.fullName
                        ?: "Account #$removedAccountId",

                removedAccountUserId =
                    removedAccount
                        ?.userId,

                removedByAccountId =
                    removerAccountId,

                removedByFullName =
                    removerAccount
                        ?.fullName
                        ?: "Account #$removerAccountId",

                removedByUserId =
                    removerAccount
                        ?.userId,

                removedByRole =
                    row[
                        UserRemovalLogs.removedByRole
                    ],

                reason =
                    row[
                        UserRemovalLogs.reason
                    ],

                removedAt =
                    row[
                        UserRemovalLogs.removedAt
                    ].toString()
            )
        }
    }

    private fun buildFullName(
        firstName: String?,
        middleName: String?,
        lastName: String?,
        userId: String?,
        accountId: Int
    ): String {
        val fullName =
            listOf(
                firstName,
                middleName,
                lastName
            )
                .mapNotNull { value ->
                    value
                        ?.trim()
                        ?.takeIf {
                            it.isNotEmpty()
                        }
                }
                .joinToString(
                    separator = " "
                )

        if (fullName.isNotBlank()) {
            return fullName
        }

        if (!userId.isNullOrBlank()) {
            return userId.trim()
        }

        return "Account #$accountId"
    }
}