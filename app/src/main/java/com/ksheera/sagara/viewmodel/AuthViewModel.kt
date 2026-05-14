package com.ksheera.sagara.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ksheera.sagara.data.AppDatabase
import com.ksheera.sagara.data.User
import com.ksheera.sagara.util.CredentialStore
import com.ksheera.sagara.util.isStrongPassword
import com.ksheera.sagara.util.isValidEmail
import com.ksheera.sagara.util.newSalt
import com.ksheera.sagara.util.saltedHash
import com.ksheera.sagara.util.sha256
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * Auth flow:
 *   1. Sign up  → validate fields → store salted+hashed password + all profile info in Room DB.
 *                 User is NOT auto-signed-in; they must verify by signing in.
 *   2. Sign in  → look up user in DB → re-hash entered password with stored salt → compare.
 *                 Falls back to legacy SHA-256 for accounts created before salting.
 *   3. Forgot   → verify secret key against DB → set new salted hash.
 */
class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val ctx = app.applicationContext
    private val dao = AppDatabase.get(app).userDao()
    val currentUser = MutableStateFlow<String?>(null)
    val message = MutableStateFlow<String?>(null)

    init {
        // Auto sign-in only if remembered AND credentials still match the DB.
        viewModelScope.launch {
            CredentialStore.load(ctx)?.let { (u, p) ->
                val user = dao.find(u.trim())
                if (user != null && verify(p, user)) {
                    dao.recordLogin(user.username)
                    currentUser.value = user.username
                }
            }
        }
    }

    private fun verify(plain: String, u: User): Boolean =
        if (u.salt.isNotBlank()) u.passwordHash == saltedHash(plain, u.salt)
        else u.passwordHash == sha256(plain) // legacy

    fun signUp(
        name: String, username: String, email: String,
        password: String, secretKey: String,
        phone: String = "",
        onDone: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val n = name.trim(); val un = username.trim(); val em = email.trim()
            val sk = secretKey.trim(); val ph = phone.trim()
            when {
                n.isBlank() || un.isBlank() || em.isBlank() || sk.isBlank() -> {
                    message.value = "Please fill all fields."; onDone(false); return@launch
                }
                un.length < 3 -> {
                    message.value = "Username must be at least 3 characters."; onDone(false); return@launch
                }
                !isValidEmail(em) -> {
                    message.value = "Enter a valid email address."; onDone(false); return@launch
                }
                !isStrongPassword(password) -> {
                    message.value = "Password must be 6+ chars with letters and numbers."; onDone(false); return@launch
                }
                sk.length < 4 -> {
                    message.value = "Secret key must be at least 4 characters."; onDone(false); return@launch
                }
                dao.find(un) != null -> {
                    message.value = "Username already exists."; onDone(false); return@launch
                }
                dao.findByEmail(em) != null -> {
                    message.value = "An account with this email already exists."; onDone(false); return@launch
                }
            }
            try {
                val salt = newSalt()
                dao.insert(
                    User(
                        username = un, name = n, email = em,
                        passwordHash = saltedHash(password, salt),
                        secretKey = sk, salt = salt, phone = ph
                    )
                )
                // Do NOT auto sign-in. Require user to verify by signing in.
                CredentialStore.clear(ctx)
                message.value = "Account created. Please sign in to continue."
                onDone(true)
            } catch (e: Exception) {
                message.value = "Could not create account: ${e.message ?: "unknown error"}"
                onDone(false)
            }
        }
    }

    fun signIn(username: String, password: String, remember: Boolean, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val key = username.trim()
            if (key.isBlank() || password.isBlank()) {
                message.value = "Enter username and password."; onDone(false); return@launch
            }
            // Verify credentials against DB (lookup by username OR email).
            val u = dao.find(key) ?: dao.findByEmail(key)
            if (u == null) { message.value = "No account found for '$key'."; onDone(false); return@launch }
            if (!verify(password, u)) { message.value = "Incorrect password."; onDone(false); return@launch }

            // Upgrade legacy unsalted accounts to salted hash on successful login.
            if (u.salt.isBlank()) {
                val salt = newSalt()
                dao.updatePassword(u.username, saltedHash(password, salt), salt)
            }
            dao.recordLogin(u.username)
            if (remember) CredentialStore.save(ctx, u.username, password) else CredentialStore.clear(ctx)
            currentUser.value = u.username
            message.value = "Welcome ${u.name}"
            onDone(true)
        }
    }

    /** Forgot-password reset using the secret key chosen at sign-up.
     *  identifier can be username or email. */
    fun resetPassword(identifier: String, secretKey: String, newPassword: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (identifier.isBlank() || secretKey.isBlank() || !isStrongPassword(newPassword)) {
                message.value = "Fill all fields. New password needs 6+ chars with letters and numbers."
                onDone(false); return@launch
            }
            val u = dao.find(identifier.trim()) ?: dao.findByEmail(identifier.trim())
            if (u == null) { message.value = "No account found"; onDone(false); return@launch }
            if (u.secretKey != secretKey.trim()) { message.value = "Secret key does not match"; onDone(false); return@launch }
            val salt = newSalt()
            dao.updatePassword(u.username, saltedHash(newPassword, salt), salt)
            CredentialStore.clear(ctx)
            message.value = "Password reset. Please sign in."
            onDone(true)
        }
    }

    fun signOut() {
        CredentialStore.clear(ctx)
        currentUser.value = null
    }
}
