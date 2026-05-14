package com.ksheera.sagara.util

import android.content.Context
import android.util.Base64

object CredentialStore {
    private const val PREFS = "ksheera_creds"
    private const val K_USER = "u"
    private const val K_PASS = "p"
    private const val K_REMEMBER = "r"

    private fun enc(s: String): String =
        Base64.encodeToString(s.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

    private fun dec(s: String?): String? = try {
        if (s == null) null else String(Base64.decode(s, Base64.NO_WRAP), Charsets.UTF_8)
    } catch (e: Exception) { null }

    fun save(ctx: Context, username: String, password: String) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(K_USER, enc(username))
            .putString(K_PASS, enc(password))
            .putBoolean(K_REMEMBER, true)
            .apply()
    }

    fun clear(ctx: Context) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun load(ctx: Context): Pair<String, String>? {
        val sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!sp.getBoolean(K_REMEMBER, false)) return null
        val u = dec(sp.getString(K_USER, null)) ?: return null
        val p = dec(sp.getString(K_PASS, null)) ?: return null
        return u to p
    }
}
