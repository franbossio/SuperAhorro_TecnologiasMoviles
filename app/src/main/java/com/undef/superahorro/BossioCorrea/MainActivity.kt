package com.undef.superahorro.BossioCorrea

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.undef.superahorro.BossioCorrea.ui.navigation.NavGraph
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme
import com.undef.superahorro.BossioCorrea.ui.theme.ThemeViewModel

class MainActivity : AppCompatActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDark      = isSystemInDarkTheme()
            val userPreference  by themeViewModel.isDarkMode.collectAsStateWithLifecycle()
            val useSystemTheme  by themeViewModel.useSystemTheme.collectAsStateWithLifecycle()

            // Si el usuario eligió "Sistema", respeta el tema del dispositivo.
            // Si eligió Claro u Oscuro manualmente, usa su elección.
            val isDark = if (useSystemTheme) systemDark else userPreference

            SuperAhorroTheme(darkTheme = isDark) {
                NavGraph(themeViewModel = themeViewModel)
            }
        }
    }
}