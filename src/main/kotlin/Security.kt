package com.hr

import com.hr.auth.config.JwtConfig
import com.hr.auth.models.AuthPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import io.ktor.server.application.install

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm =
                JwtConfig.realm

            verifier(
                JwtConfig.verifier
            )

            validate { credential ->
                val accountId =
                    credential.payload
                        .getClaim("accountId")
                        .asInt()

                val userId =
                    credential.payload
                        .getClaim("userId")
                        .asString()

                val role =
                    credential.payload
                        .getClaim("role")
                        .asString()

                val tokenId =
                    credential.payload.id

                val tokenType =
                    credential.payload
                        .getClaim("tokenType")
                        .asString()

                val validAudience =
                    credential.payload
                        .audience
                        .contains(
                            JwtConfig.audience
                        )

                if (
                    accountId != null &&
                    !role.isNullOrBlank() &&
                    !tokenId.isNullOrBlank() &&
                    tokenType == "access" &&
                    validAudience
                ) {
                    AuthPrincipal(
                        accountId =
                            accountId,
                        userId =
                            userId,
                        role =
                            role,
                        tokenId =
                            tokenId
                    )
                } else {
                    null
                }
            }

            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf(
                        "detail" to
                                "Authentication credentials are invalid or expired."
                    )
                )
            }
        }
    }

    println(
        "JWT authentication configured: auth-jwt"
    )
}