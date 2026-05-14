package com.ksheera.sagara.util

import java.security.MessageDigest
import java.security.SecureRandom

fun sha256(s: String): String =
    MessageDigest.getInstance("SHA-256").digest(s.toByteArray()).joinToString("") { "%02x".format(it) }

/** PBKDF2-style salted SHA-256 with multiple rounds to slow brute-force. */
fun saltedHash(password: String, salt: String, rounds: Int = 12_000): String {
    var data = (salt + ":" + password).toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    repeat(rounds) { data = md.digest(data) }
    return data.joinToString("") { "%02x".format(it) }
}

fun newSalt(bytes: Int = 16): String {
    val b = ByteArray(bytes)
    SecureRandom().nextBytes(b)
    return b.joinToString("") { "%02x".format(it) }
}

/** Basic email format check. */
fun isValidEmail(e: String): Boolean =
    Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(e.trim())

/** Password must be at least 6 chars and contain a letter and a digit. */
fun isStrongPassword(p: String): Boolean =
    p.length >= 6 && p.any { it.isLetter() } && p.any { it.isDigit() }
