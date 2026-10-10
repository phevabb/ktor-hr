package com.hr.password.table

import com.hr.account.table.Accounts
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.javatime.CurrentDateTime
import org.jetbrains.exposed.v1.javatime.datetime

object PasswordResetTokens :
    IntIdTable(
        name =
            "password_reset_tokens"
    ) {

    val accountId =
        reference(
            name =
                "account_id",

            foreign =
                Accounts,

            onDelete =
                ReferenceOption.CASCADE
        )
            .index()

    val tokenHash =
        varchar(
            name =
                "token_hash",

            length =
                64
        )
            .uniqueIndex()

    val expiresAt =
        datetime(
            name =
                "expires_at"
        )
            .index()

    val usedAt =
        datetime(
            name =
                "used_at"
        )
            .nullable()

    val createdAt =
        datetime(
            name =
                "created_at"
        )
            .defaultExpression(
                CurrentDateTime
            )
}