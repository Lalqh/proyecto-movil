package com.example.proyecto.Utils

import org.mindrot.jbcrypt.BCrypt


object EncryptionUtils {
    fun encryptPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun checkPassword(plainPassword: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(plainPassword, hashedPassword)
    }
}