package com.hr

import com.hr.academicqualification.academicQualificationModule
import com.hr.account.accountModule
import com.hr.admin.adminModule
import com.hr.auth.authModule
import com.hr.changeofgrade.changeOfGradeModule
import com.hr.classes.classesModule
import com.hr.cloudinary.cloudinaryModule
import com.hr.config.DatabaseFactory
import com.hr.currentgrade.currentGradeModule
import com.hr.department.departmentModule
import com.hr.districts.districtModule
import com.hr.managementUnits.managementUnitModule
import com.hr.managerprofile.managerProfileModule
import com.hr.media.mediaModule
import com.hr.nextgrade.configureNextGradeModule
import com.hr.onleavetype.onLeaveTypeModule
import com.hr.position.positionModule
import com.hr.region.regionModule
import com.hr.staff.staffModule
import com.hr.staffclass.staffClassModule
import com.hr.title.titleModule
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}


fun Application.module() {

    println("MODULE LOADED")

    DatabaseFactory.init()
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


}










