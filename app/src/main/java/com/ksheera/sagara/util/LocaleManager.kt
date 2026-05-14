package com.ksheera.sagara.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

enum class Lang { EN, KN }

object LocaleManager {
    val lang = mutableStateOf(Lang.EN)
    fun toggle() { lang.value = if (lang.value == Lang.EN) Lang.KN else Lang.EN }
}

/** Pick English/Kannada based on current selection. */
@Composable
fun tr(en: String, kn: String): String =
    if (LocaleManager.lang.value == Lang.EN) en else kn
