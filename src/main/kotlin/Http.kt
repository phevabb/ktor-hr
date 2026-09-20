package com.hr

import com.hr.config.configureCors
import io.ktor.server.application.*
import io.ktor.server.plugins.defaultheaders.*



fun Application.configureHttp() {

    configureCors()

    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
    }
}
