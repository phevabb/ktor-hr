package com.hr.manager.repositories

import com.hr.account.dtos.AccountCreateRequest
import com.hr.account.dtos.AccountResponse
import com.hr.account.repositories.AccountRepository
import com.hr.account.table.Accounts
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll

object ManagerCreateUserRepository {

    suspend fun findManagerAccount(
        accountId: Int
    ): ResultRow? {
        println(
            "Finding Manager account for account creation"
        )

        println(
            "Manager account ID: $accountId"
        )

        val managerRow =
            Accounts
                .selectAll()
                .where {
                    Accounts.id eq
                            accountId
                }
                .firstOrNull()

        println(
            "Manager account found: ${managerRow != null}"
        )

        return managerRow
    }

    suspend fun userIdExists(
        userId: String
    ): Boolean {
        val normalizedUserId =
            userId.trim()

        println(
            "Checking whether user ID already exists"
        )

        println(
            "User ID: $normalizedUserId"
        )

        val exists =
            AccountRepository
                .userIdExists(
                    normalizedUserId
                )

        println(
            "User ID already exists: $exists"
        )

        return exists
    }

    suspend fun phoneNumberExists(
        phoneNumber: String
    ): Boolean {
        val normalizedPhoneNumber =
            phoneNumber.trim()

        println(
            "Checking whether phone number already exists"
        )

        val exists =
            AccountRepository
                .phoneNumberExists(
                    normalizedPhoneNumber
                )

        println(
            "Phone number already exists: $exists"
        )

        return exists
    }

    suspend fun createAccount(
        request: AccountCreateRequest
    ): Int {
        println(
            "Creating account through AccountRepository"
        )

        println(
            "User ID: ${request.userId}"
        )

        println(
            "Role: ${request.role}"
        )

        println(
            "Region ID: ${request.regionId}"
        )

        println(
            "AccountRepository will generate the default password hash"
        )

        val accountId =
            AccountRepository
                .create(
                    request =
                        request
                )

        println(
            "Account created successfully"
        )

        println(
            "Created account ID: $accountId"
        )

        return accountId
    }

    suspend fun getAccountById(
        accountId: Int
    ): AccountResponse? {
        println(
            "Retrieving newly created account"
        )

        println(
            "Created account ID: $accountId"
        )

        val account =
            AccountRepository
                .getById(
                    accountId
                )

        println(
            "Created account retrieved: ${account != null}"
        )

        return account
    }
}