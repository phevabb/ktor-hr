package com.hr.account.security


import at.favre.lib.crypto.bcrypt.BCrypt

object AccountPassword {

    const val DEFAULT_PASSWORD = "Securepassword123!"

    fun hashDefaultPassword(): String {
        return BCrypt.withDefaults()
            .hashToString(
                12,
                DEFAULT_PASSWORD.toCharArray()
            )
    }

    fun verify(
        password: String,
        passwordHash: String
    ): Boolean {
        return BCrypt.verifyer()
            .verify(
                password.toCharArray(),
                passwordHash
            )
            .verified
    }
}
