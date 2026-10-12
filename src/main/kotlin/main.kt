package com.hr

import com.hr.academicqualification.academicQualificationModule
import com.hr.academicqualification.seed.AcademicQualificationSeeder
import com.hr.account.accountModule
import com.hr.admin.adminModule

import com.hr.auth.authModule
import com.hr.changeofgrade.changeOfGradeModule
import com.hr.changeofgrade.seed.ChangeOfGradeSeeder
import com.hr.classes.classesModule
import com.hr.classes.seed.CategorySeeder
import com.hr.cloudinary.cloudinaryModule
import com.hr.config.DatabaseFactory
import com.hr.currentgrade.currentGradeModule
import com.hr.currentgrade.seed.CurrentGradeSeeder
import com.hr.department.departmentModule
import com.hr.department.seed.DirectorateSeeder
import com.hr.districts.districtModule
import com.hr.districts.seed.DistrictSeeder
import com.hr.managementUnits.managementUnitModule
import com.hr.managementUnits.seed.ManagementUnitSeeder
import com.hr.manager.managerModule
import com.hr.managerprofile.managerProfileModule
import com.hr.media.mediaModule
import com.hr.nextgrade.configureNextGradeModule
import com.hr.nextgrade.seed.NextGradeSeeder
import com.hr.onleavetype.onLeaveTypeModule
import com.hr.password.passwordModule
import com.hr.position.positionModule
import com.hr.region.regionModule
import com.hr.region.seed.RegionSeeder
import com.hr.removallogs.configureRemovalLogs
import com.hr.staff.staffModule
import com.hr.staffclass.staffClassModule
import com.hr.title.seed.TitleSeeder
import com.hr.title.titleModule
import io.ktor.server.application.Application
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}


fun Application.module() {

    println("MODULE LOADED")

    DatabaseFactory.init()




    runBlocking {
        try {
//            TitleSeeder.seed()
//            ManagementUnitSeeder.seed()
//            DistrictSeeder.seed()
//            RegionSeeder.seed()
//            DirectorateSeeder.seed()
            // ChangeOfGradeSeeder.seed()
//            NextGradeSeeder.seed()
            //CurrentGradeSeeder.seed()
//            CategorySeeder.seed()
//            AcademicQualificationSeeder.seed()
        } catch (exception: Exception) {
            println(
                "seeding failed"
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

            exception.printStackTrace()

            println(
                "The application will continue starting"
            )

            println(
                "=================================================="
            )
        }
    }



    configureHttp()
    configureSerialization()
    configureSecurity()
    configureStatusPages()
    configureRouting()

    regionModule()
    districtModule()
    departmentModule()
    classesModule()
    managementUnitModule()
    currentGradeModule()
    academicQualificationModule()
    changeOfGradeModule()
    titleModule()
    onLeaveTypeModule()
    staffClassModule()
    positionModule()
    accountModule()
    configureNextGradeModule()
    cloudinaryModule()
    mediaModule()
    managerProfileModule()
    authModule()
    staffModule()
    adminModule()
    managerModule()
    configureRemovalLogs()
    passwordModule()





}










