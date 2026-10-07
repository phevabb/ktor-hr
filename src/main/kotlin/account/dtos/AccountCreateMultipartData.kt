package com.hr.account.dtos

data class AccountCreateMultipartData(
    val request: AccountCreateRequest,

    val profilePicture:
    AccountProfilePictureUpload?
)

data class AccountProfilePictureUpload(
    val originalFileName:
    String?,

    val contentType:
    String?,

    val bytes:
    ByteArray
)