package com.hr.manager.mappers

import com.hr.account.table.Accounts
import com.hr.manager.dtos.AdminFilteredUserResponse

import org.jetbrains.exposed.v1.core.ResultRow

object AdminFilteredUserMapper {

    fun toResponse(
        row: ResultRow
    ): AdminFilteredUserResponse {
        val firstName =
            row[Accounts.firstName]
                ?.trim()
                ?.takeIf { value ->
                    value.isNotBlank()
                }

        val lastName =
            row[Accounts.lastName]
                ?.trim()
                ?.takeIf { value ->
                    value.isNotBlank()
                }

        val fullName =
            listOfNotNull(
                firstName,
                lastName
            )
                .joinToString(" ")
                .trim()
                .takeIf { value ->
                    value.isNotBlank()
                }

        return AdminFilteredUserResponse(
            id =
                row[Accounts.id].value,

            userId =
                row[Accounts.userId],

            firstName =
                firstName,

            lastName =
                lastName,

            fullName =
                fullName,

            phoneNumber =
                row[Accounts.phoneNumber],

            profilePictureUrl =
                row[Accounts.profilePictureUrl]
        )
    }
}