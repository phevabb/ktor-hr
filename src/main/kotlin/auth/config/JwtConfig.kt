package com.hr.auth.config



import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.hr.config.EnvironmentConfig
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.UUID

object JwtConfig {

    val issuer: String =
        EnvironmentConfig.getOrDefault(
            name = "JWT_ISSUER",
            defaultValue = "ktor-hr-api"
        )
    val audience: String =
        EnvironmentConfig.getOrDefault(
            name = "JWT_AUDIENCE",
            defaultValue = "ktor-hr-api"
        )


    val realm: String =
        EnvironmentConfig.getOrDefault(
            name = "JWT_REALM",
            defaultValue = "ktor-hr"
        )

    private val secret: String =
        EnvironmentConfig.required(
            "JWT_SECRET"
        )

    private val accessTokenMinutes: Long =
        EnvironmentConfig.getOrDefault(
            name = "JWT_ACCESS_TOKEN_MINUTES",
            defaultValue = "60"
        ).toLongOrNull() ?: 60L

    private val passwordResetMinutes: Long =
        EnvironmentConfig.getOrDefault(
            name = "JWT_PASSWORD_RESET_MINUTES",
            defaultValue = "20"
        ).toLongOrNull() ?: 20L

    private val algorithm: Algorithm =
        Algorithm.HMAC256(secret)

    val verifier: JWTVerifier =
        JWT.require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim(
                "tokenType",
                "access"
            )
            .build()

    fun generateAccessToken(
        accountId: Int,
        userId: String?,
        role: String
    ): GeneratedJwtToken {
        val now =
            Instant.now()

        val expiresAt =
            now.plus(
                accessTokenMinutes,
                ChronoUnit.MINUTES
            )

        val tokenId =
            UUID.randomUUID()
                .toString()

        val token =
            JWT.create()
                .withIssuer(issuer)
                .withAudience(audience)
                .withSubject(
                    accountId.toString()
                )
                .withJWTId(tokenId)
                .withIssuedAt(
                    Date.from(now)
                )
                .withExpiresAt(
                    Date.from(expiresAt)
                )
                .withClaim(
                    "accountId",
                    accountId
                )
                .withClaim(
                    "userId",
                    userId
                )
                .withClaim(
                    "role",
                    role
                )
                .withClaim(
                    "tokenType",
                    "access"
                )
                .sign(algorithm)

        return GeneratedJwtToken(
            token = token,
            tokenId = tokenId,
            expiresAt = expiresAt
        )
    }

    fun generatePasswordResetToken(
        accountId: Int,
        passwordVersion: String
    ): GeneratedJwtToken {
        val now =
            Instant.now()

        val expiresAt =
            now.plus(
                passwordResetMinutes,
                ChronoUnit.MINUTES
            )

        val tokenId =
            UUID.randomUUID()
                .toString()

        val token =
            JWT.create()
                .withIssuer(issuer)
                .withAudience(audience)
                .withSubject(
                    accountId.toString()
                )
                .withJWTId(tokenId)
                .withIssuedAt(
                    Date.from(now)
                )
                .withExpiresAt(
                    Date.from(expiresAt)
                )
                .withClaim(
                    "accountId",
                    accountId
                )
                .withClaim(
                    "passwordVersion",
                    passwordVersion
                )
                .withClaim(
                    "tokenType",
                    "password_reset"
                )
                .sign(algorithm)

        return GeneratedJwtToken(
            token = token,
            tokenId = tokenId,
            expiresAt = expiresAt
        )
    }

    fun verifyPasswordResetToken(
        token: String
    ) =
        JWT.require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim(
                "tokenType",
                "password_reset"
            )
            .build()
            .verify(token)



data class GeneratedJwtToken(
    val token: String,
    val tokenId: String,
    val expiresAt: Instant
)}