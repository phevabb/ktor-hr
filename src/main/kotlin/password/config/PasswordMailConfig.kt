package com.hr.password.config

import io.github.cdimascio.dotenv.dotenv

object PasswordMailConfig {

    private val environment =
        dotenv {
            ignoreIfMissing = true
        }

    private fun requiredValue(
        name: String
    ): String {
        return environment[name]
            ?: System.getenv(name)
            ?: throw IllegalStateException(
                "Missing required environment variable: $name"
            )
    }

    private fun optionalValue(
        name: String,
        defaultValue: String
    ): String {
        return environment[name]
            ?: System.getenv(name)
            ?: defaultValue
    }

    val host: String =
        optionalValue(
            name = "EMAIL_HOST",
            defaultValue = "smtp.gmail.com"
        )

    val username: String =
        requiredValue(
            name = "EMAIL_HOST_USER"
        )

    val password: String =
        requiredValue(
            name = "EMAIL_HOST_PASSWORD"
        )

    val port: Int =
        optionalValue(
            name = "EMAIL_PORT",
            defaultValue = "465"
        ).toIntOrNull()
            ?: throw IllegalStateException(
                "EMAIL_PORT must be a valid integer."
            )

    val useSsl: Boolean =
        optionalValue(
            name = "EMAIL_USE_SSL",
            defaultValue = "true"
        ).toBooleanStrictOrNull()
            ?: throw IllegalStateException(
                "EMAIL_USE_SSL must be true or false."
            )

    val defaultFromEmail: String =
        optionalValue(
            name = "EMAIL_FROM",
            defaultValue = username
        )

    val frontendBaseUrl: String =
        requiredValue(
            name = "FRONTEND_BASE_URL"
        ).trimEnd('/')
}