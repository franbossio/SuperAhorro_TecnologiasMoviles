package com.undef.superahorro.BossioCorrea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.undef.superahorro.BossioCorrea.ui.navigation.NavGraph
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperAhorroTheme {
                NavGraph()
            }
        }
    }
}