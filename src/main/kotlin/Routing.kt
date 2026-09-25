package com.hr

import com.hr.auth.models.AuthPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        /*
         * Public health-check route
         */
        get("/") {
            call.respondText(
                "HR API is running"
            )
        }

        /*
         * Public JSON test route
         */
        get("/json/kotlinx-serialization") {
            call.respond(
                HttpStatusCode.OK,
                mapOf(
                    "hello" to "world"
                )
            )
        }

        /*
         * Optional JWT-protected test route.
         *
         * This verifies that your JWT configuration
         * and Authorization header are working.
         */
        authenticate("auth-jwt") {
            get("/api/auth/protected-test") {
                val principal =
                    call.principal<
                            AuthPrincipal
                            >()
                        ?: return@get call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf(
                                "detail" to
                                        "Authentication is required."
                            )
                        )

                call.respond(
                    HttpStatusCode.OK,
                    mapOf(
                        "message" to
                                "JWT authentication is working.",

                        "accountId" to
                                principal.accountId.toString(),

                        "userId" to
                                principal.userId.orEmpty(),

                        "role" to
                                principal.role,

                        "tokenId" to
                                principal.tokenId
                    )
                )
            }
        }
    }

    println(
        "General routing configured"
    )
}