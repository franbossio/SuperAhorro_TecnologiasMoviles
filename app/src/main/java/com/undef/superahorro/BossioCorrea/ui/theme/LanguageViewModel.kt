package com.undef.superahorro.BossioCorrea.ui.theme

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LanguageViewModel(application: Application) : AndroidViewModel(application) {

    private val _languageCode = MutableStateFlow(currentLanguageCode())
    val languageCode = _languageCode.asStateFlow()

    fun setLanguage(code: String) {
        _languageCode.value = code
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
    }

    private fun currentLanguageCode(): String {
        val appLocales = AppCompatDelegate.getApplicationLocales()
        return if (!appLocales.isEmpty) {
            appLocales[0]?.language ?: "es"
        } else {
            "es"
        }
    }
}
