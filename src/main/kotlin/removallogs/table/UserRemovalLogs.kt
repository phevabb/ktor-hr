package com.hr.removallogs.table

import com.hr.account.table.Accounts
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.javatime.CurrentDateTime
import org.jetbrains.exposed.v1.javatime.datetime

object UserRemovalLogs :
    IntIdTable(
        name =
            "user_removal_logs"
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

    val removedByAccountId =
        reference(
            name =
                "removed_by_account_id",

            foreign =
                Accounts,

            onDelete =
                ReferenceOption.RESTRICT
        )
            .index()

    val removedByRole =
        varchar(
            name =
                "removed_by_role",

            length =
                50
        )

    val reason =
        varchar(
            name =
                "reason",

            length =
                255
        )

    val removedAt =
        datetime(
            name =
                "removed_at"
        )
            .defaultExpression(
                CurrentDateTime
            )

    init {
        index(
            isUnique =
                false,

            accountId,
            removedAt
        )

        index(
            isUnique =
                false,

            removedByAccountId,
            removedAt
        )
    }
}