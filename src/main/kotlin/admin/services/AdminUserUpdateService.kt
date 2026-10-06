package com.hr.admin.services

import com.hr.admin.dtos.AdminProfilePictureUpload
import com.hr.admin.dtos.AdminUserUpdateRequest
import com.hr.admin.repositories.AdminUserUpdateRepository
import com.hr.auth.repositories.AuthRepository

object AdminUserUpdateService {

    suspend fun updateUser(
        authenticatedAccountId: Int,
        targetAccountId: Int,
        request: AdminUserUpdateRequest,
        profilePicture:
        AdminProfilePictureUpload?,
        profilePictureStorage:
        AdminProfilePictureStorage
    ): AdminUserUpdateResult {
        println(
            "=================================================="
        )

        println(
            "Admin user-update service started"
        )

        println(
            "Authenticated account ID: $authenticatedAccountId"
        )

        println(
            "Target account ID: $targetAccountId"
        )

        val authenticatedAccount =
            AuthRepository
                .findAccountById(
                    authenticatedAccountId
                )
                ?: return AdminUserUpdateResult
                    .AuthenticatedAccountNotFound

        if (!authenticatedAccount.isActive) {
            println(
                "User update rejected"
            )

            println(
                "Reason: Authenticated account is inactive"
            )

            println(
                "=================================================="
            )

            return AdminUserUpdateResult
                .AuthenticatedAccountInactive
        }

        val existingRow =
            AdminUserUpdateRepository
                .findAccountRow(
                    targetAccountId
                )
                ?: run {
                    println(
                        "Target account was not found"
                    )

                    println(
                        "Target account ID: $targetAccountId"
                    )

                    println(
                        "=================================================="
                    )

                    return AdminUserUpdateResult
                        .AccountNotFound
                }

        val validationErrors =
            validateRequest(
                accountId =
                    targetAccountId,

                request =
                    request,

                profilePicture =
                    profilePicture
            )

        if (validationErrors.isNotEmpty()) {
            return AdminUserUpdateResult
                .ValidationFailed(
                    errors =
                        validationErrors
                )
        }

        return try {
            var finalRequest =
                request

            val existingPublicId =
                existingRow[
                    com.hr.account.table.Accounts
                        .profilePicturePublicId
                ]

            if (
                request.removeProfilePicture
            ) {
                if (
                    !existingPublicId.isNullOrBlank()
                ) {
                    runCatching {
                        profilePictureStorage
                            .delete(
                                existingPublicId
                            )
                    }.onFailure { exception ->
                        println(
                            "Unable to delete old profile picture"
                        )

                        println(
                            "Error: ${exception.message}"
                        )
                    }
                }

                finalRequest =
                    finalRequest.copy(
                        providedFields =
                            finalRequest
                                .providedFields +
                                    "profilePictureUrl",

                        profilePictureUrl =
                            null,

                        profilePicturePublicId =
                            null
                    )
            } else if (
                profilePicture != null
            ) {
                val uploadedPicture =
                    profilePictureStorage
                        .upload(
                            accountId =
                                targetAccountId,

                            upload =
                                profilePicture
                        )

                if (
                    !existingPublicId.isNullOrBlank() &&
                    existingPublicId !=
                    uploadedPicture.publicId
                ) {
                    runCatching {
                        profilePictureStorage
                            .delete(
                                existingPublicId
                            )
                    }.onFailure { exception ->
                        println(
                            "Unable to delete previous profile picture"
                        )

                        println(
                            "Error: ${exception.message}"
                        )
                    }
                }

                finalRequest =
                    finalRequest.copy(
                        providedFields =
                            finalRequest
                                .providedFields +
                                    "profilePictureUrl",

                        profilePictureUrl =
                            uploadedPicture.url,

                        profilePicturePublicId =
                            uploadedPicture.publicId
                    )
            }

            val updated =
                AdminUserUpdateRepository
                    .updateAccount(
                        accountId =
                            targetAccountId,

                        request =
                            finalRequest
                    )

            if (!updated) {
                return AdminUserUpdateResult
                    .AccountNotFound
            }

            val updatedAccount =
                AdminUserUpdateRepository
                    .getUpdatedAccount(
                        targetAccountId
                    )
                    ?: return AdminUserUpdateResult
                        .AccountNotFound

            println(
                "User updated successfully"
            )

            println(
                "Account ID: ${updatedAccount.id}"
            )

            println(
                "User ID: ${updatedAccount.userId}"
            )

            println(
                "Updated field count: ${finalRequest.providedFields.size}"
            )

            println(
                "=================================================="
            )

            AdminUserUpdateResult.Success(
                account =
                    updatedAccount
            )
        } catch (exception: Exception) {
            println(
                "User update failed"
            )

            println(
                "Target account ID: $targetAccountId"
            )

            println(
                "Error type: ${exception::class.simpleName}"
            )

            println(
                "Error message: ${exception.message}"
            )

            exception.cause?.let { cause ->
                println(
                    "Cause type: ${cause::class.simpleName}"
                )

                println(
                    "Cause message: ${cause.message}"
                )
            }

            println(
                "=================================================="
            )

            AdminUserUpdateResult
                .Failed
        }
    }

    private suspend fun validateRequest(
        accountId: Int,
        request: AdminUserUpdateRequest,
        profilePicture:
        AdminProfilePictureUpload?
    ): Map<String, List<String>> {
        val errors =
            linkedMapOf<String, List<String>>()

        if (
            request.providedFields.isEmpty() &&
            profilePicture == null
        ) {
            errors[
                "detail"
            ] =
                listOf(
                    "At least one field is required."
                )
        }

        if (
            request.providedFields.contains(
                "userId"
            )
        ) {
            val userId =
                request.userId
                    ?.trim()
                    .orEmpty()

            if (userId.isBlank()) {
                errors[
                    "userId"
                ] =
                    listOf(
                        "Staff ID cannot be blank."
                    )
            } else if (
                AdminUserUpdateRepository
                    .userIdBelongsToAnotherAccount(
                        accountId =
                            accountId,

                        userId =
                            userId
                    )
            ) {
                errors[
                    "userId"
                ] =
                    listOf(
                        "This Staff ID is assigned to another account."
                    )
            }
        }

        if (
            profilePicture != null
        ) {
            if (
                profilePicture.bytes.size >
                2 * 1024 * 1024
            ) {
                errors[
                    "profilePictureUrl"
                ] =
                    listOf(
                        "The profile picture must be 2 MB or smaller."
                    )
            }

            if (
                profilePicture.contentType
                    ?.startsWith(
                        prefix =
                            "image/",

                        ignoreCase =
                            true
                    ) !=
                true
            ) {
                errors[
                    "profilePictureUrl"
                ] =
                    listOf(
                        "The selected file must be an image."
                    )
            }
        }

        return errors
    }
}