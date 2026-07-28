package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object AppThemeState {
    var isDark by mutableStateOf(true)
}

// Background: Warm soft slate in light mode (eye-friendly, not blinding stark white #FFFFFF)
val DarkBackground: Color
    @Composable get() = if (AppThemeState.isDark) Color(0xFF0B0813) else Color(0xFFE2E8F0)

// Cards/Surfaces: Soft clean surface
val DarkSurface: Color
    @Composable get() = if (AppThemeState.isDark) Color(0xFF151221) else Color(0xFFF1F5F9)

// Surface Variant (chips, input containers, sub-cards):
val DarkSurfaceVariant: Color
    @Composable get() = if (AppThemeState.isDark) Color(0xFF211B34) else Color(0xFFCBD5E1)

// Borders: Distinct outline in light mode
val DarkBorder: Color
    @Composable get() = if (AppThemeState.isDark) Color(0xFF332A52) else Color(0xFF94A3B8)

// Primary Violet:
val VioletPrimary = Color(0xFFA855F7)

// Violet Light: In light mode, map to rich deep purple (0xFF6B21A8) so headers & accents are bold and clear
val VioletLight: Color
    @Composable get() = if (AppThemeState.isDark) Color(0xFFC084FC) else Color(0xFF6B21A8)

val VioletDark = Color(0xFF7E22CE)

val AmberRating = Color(0xFFF59E0B)
val AmberRatingLight = Color(0xFFFBBF24)

// Primary Text: Crisp black/charcoal in light mode
val TextPrimary: Color
    @Composable get() = if (AppThemeState.isDark) Color(0xFFF3F0FF) else Color(0xFF0F172A)

// Secondary Text: Dark slate gray in light mode (very bold and legible, never faint)
val TextSecondary: Color
    @Composable get() = if (AppThemeState.isDark) Color(0xFF9CA3AF) else Color(0xFF1E293B)

// Muted Text: Medium dark slate gray in light mode
val TextMuted: Color
    @Composable get() = if (AppThemeState.isDark) Color(0xFF6B7280) else Color(0xFF334155)

val StatusWatched = Color(0xFF10B981)   // Green
val StatusWatching = Color(0xFF3B82F6)  // Blue
val StatusToWatch = Color(0xFFA855F7)   // Purple


