package com.hr.config

import io.github.cdimascio.dotenv.dotenv

object EnvironmentConfig {

    private val dotenv by lazy {
        dotenv {
            directory = "./"
            ignoreIfMissing = true
            ignoreIfMalformed = false
        }
    }

    fun required(
        name: String
    ): String {
        return get(name)
            ?.takeIf {
                it.isNotBlank()
            }
            ?: error(
                "Required environment variable $name is missing."
            )
    }

    fun get(
        name: String
    ): String? {
        return System.getenv(name)
            ?.trim()
            ?.takeIf {
                it.isNotBlank()
            }
            ?: dotenv[name]
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }
    }

    fun getOrDefault(
        name: String,
        defaultValue: String
    ): String {
        return get(name)
            ?: defaultValue
    }

    fun getLongOrDefault(
        name: String,
        defaultValue: Long
    ): Long {
        return get(name)
            ?.toLongOrNull()
            ?: defaultValue
    }
}