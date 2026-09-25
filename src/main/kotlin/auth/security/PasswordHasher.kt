package com.hr.auth.security




import org.mindrot.jbcrypt.BCrypt
import java.security.MessageDigest

object PasswordHasher {

    fun hash(
        plainPassword: String
    ): String {
        return BCrypt.hashpw(
            plainPassword,
            BCrypt.gensalt(12)
        )
    }

    fun verify(
        plainPassword: String,
        passwordHash: String
    ): Boolean {
        return try {
            BCrypt.checkpw(
                plainPassword,
                passwordHash
            )
        } catch (exception: Exception) {
            println(
                "Password verification failed: ${exception.message}"
            )

            false
        }
    }

    fun passwordVersion(
        passwordHash: String
    ): String {
        val digest =
            MessageDigest.getInstance(
                "SHA-256"
            )

        return digest
            .digest(
                passwordHash.toByteArray()
            )
            .joinToString("") { byte ->
                "%02x".format(byte)
            }
    }
}
