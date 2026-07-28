package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun SeyirDefteriTheme(
    isDark: Boolean = AppThemeState.isDark,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = VioletPrimary,
            onPrimary = Color.White,
            primaryContainer = VioletDark,
            onPrimaryContainer = VioletLight,
            secondary = AmberRating,
            onSecondary = Color.Black,
            secondaryContainer = Color(0xFF3B2D00),
            onSecondaryContainer = AmberRatingLight,
            background = Color(0xFF0B0813),
            onBackground = Color(0xFFF3F0FF),
            surface = Color(0xFF151221),
            onSurface = Color(0xFFF3F0FF),
            surfaceVariant = Color(0xFF211B34),
            onSurfaceVariant = Color(0xFF9CA3AF),
            outline = Color(0xFF332A52),
            outlineVariant = Color(0xFF2A2342)
        )
    } else {
        lightColorScheme(
            primary = VioletPrimary,
            onPrimary = Color.White,
            primaryContainer = VioletLight,
            onPrimaryContainer = VioletDark,
            secondary = AmberRating,
            onSecondary = Color.Black,
            secondaryContainer = Color(0xFFFEF3C7),
            onSecondaryContainer = AmberRating,
            background = Color(0xFFECEFF4),
            onBackground = Color(0xFF0F172A),
            surface = Color(0xFFF8FAFC),
            onSurface = Color(0xFF0F172A),
            surfaceVariant = Color(0xFFE2E8F0),
            onSurfaceVariant = Color(0xFF1E293B),
            outline = Color(0xFFCBD5E1),
            outlineVariant = Color(0xFF94A3B8)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

