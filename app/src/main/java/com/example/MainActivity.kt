package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.navigation.MainScreen
import com.example.ui.theme.AppThemeState
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.SeyirDefteriTheme
import com.example.ui.viewmodel.SeyirDefteriViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: SeyirDefteriViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDark by viewModel.isDarkMode.collectAsState()
            AppThemeState.isDark = isDark

            SeyirDefteriTheme(isDark = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}
