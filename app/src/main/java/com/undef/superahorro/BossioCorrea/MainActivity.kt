package com.undef.superahorro.BossioCorrea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.undef.superahorro.BossioCorrea.ui.navigation.NavGraph
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme
import com.undef.superahorro.BossioCorrea.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {

    // ViewModel de ámbito Activity: vive mientras la Activity exista
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDark by themeViewModel.isDarkMode.collectAsStateWithLifecycle()

            SuperAhorroTheme(darkTheme = isDark) {
                NavGraph(themeViewModel = themeViewModel)
            }
        }
    }
}