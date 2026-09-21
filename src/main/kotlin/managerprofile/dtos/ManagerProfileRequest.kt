package com.hr.managerprofile.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ManagerProfileRequest(
    val accountId: Int,
    val regionId: Int
)
