package com.hr.password.security

import at.favre.lib.crypto.bcrypt.BCrypt
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object PasswordSecurity {

    private val secureRandom =
        SecureRandom()

    fun hashPassword(
        plainPassword: String
    ): String {
        return BCrypt
            .withDefaults()
            .hashToString(
                12,
                plainPassword.toCharArray()
            )
    }

    fun verifyPassword(
        plainPassword: String,
        storedPasswordHash: String
    ): Boolean {
        return BCrypt
            .verifyer()
            .verify(
                plainPassword.toCharArray(),
                storedPasswordHash
                    .toCharArray()
            )
            .verified
    }

    fun generateResetToken(): String {
        val tokenBytes =
            ByteArray(
                32
            )

        secureRandom.nextBytes(
            tokenBytes
        )

        return Base64
            .getUrlEncoder()
            .withoutPadding()
            .encodeToString(
                tokenBytes
            )
    }

    fun hashResetToken(
        token: String
    ): String {
        val digest =
            MessageDigest
                .getInstance(
                    "SHA-256"
                )
                .digest(
                    token.toByteArray(
                        Charsets.UTF_8
                    )
                )

        return digest.joinToString(
            separator =
                ""
        ) { byte ->
            "%02x".format(
                byte
            )
        }
    }

    fun validateNewPassword(
        password: String
    ): String? {
        if (
            password.length <
            8
        ) {
            return "The password must contain at least 8 characters."
        }

        if (
            password.length >
            128
        ) {
            return "The password cannot exceed 128 characters."
        }

        if (
            password.none {
                it.isUpperCase()
            }
        ) {
            return "The password must contain an uppercase letter."
        }

        if (
            password.none {
                it.isLowerCase()
            }
        ) {
            return "The password must contain a lowercase letter."
        }

        if (
            password.none {
                it.isDigit()
            }
        ) {
            return "The password must contain a number."
        }

        return null
    }
}