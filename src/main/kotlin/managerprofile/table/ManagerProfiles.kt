package com.hr.managerprofile.table



import com.hr.account.table.Accounts
import com.hr.region.tables.Regions
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object ManagerProfiles : IntIdTable("manager_profiles") {

    val accountId = reference(
        name = "account_id",
        foreign = Accounts,
        onDelete = ReferenceOption.CASCADE
    ).uniqueIndex()

    val regionId = reference(
        name = "region_id",
        foreign = Regions,
        onDelete = ReferenceOption.RESTRICT
    )
}