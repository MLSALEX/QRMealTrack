package com.example.qrmealtrack.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.example.qrmealtrack.ui.theme.stats.LocalStatsColors
import com.example.qrmealtrack.ui.theme.stats.NeonStatsColors
import com.example.qrmealtrack.ui.theme.stats.StatsColors

fun toMaterialColorScheme(statsColors: StatsColors, isDark: Boolean): ColorScheme {
    return if (isDark) {
        darkColorScheme( // ✅ это функция, не класс
            primary = statsColors.glowBlue,
            onPrimary = statsColors.textPrimary,
            primaryContainer = statsColors.cardBackground,
            onPrimaryContainer = statsColors.textPrimary,
            secondary = statsColors.glowRed,
            onSecondary = statsColors.textSecondary,
            secondaryContainer = statsColors.cardBackground,
            onSecondaryContainer = statsColors.textSecondary,
            tertiary = Color(0xFF00E676),
            onTertiary = Color.Black,
            tertiaryContainer = statsColors.cardBackground,
            onTertiaryContainer = statsColors.textPrimary,
            background = statsColors.background,
            onBackground = statsColors.textPrimary,
            surface = statsColors.cardBackground,
            onSurface = statsColors.textPrimary,
            surfaceVariant = statsColors.cardBackground,
            onSurfaceVariant = statsColors.textSecondary,
            error = Color(0xFFB00020),
            onError = Color.White,
            errorContainer = Color(0xFFCF6679),
            onErrorContainer = Color.Black,
            outline = statsColors.textSecondary,
            outlineVariant = statsColors.textSecondary.copy(alpha = 0.5f),
            scrim = Color.Black,
            inverseSurface = statsColors.background,
            inverseOnSurface = statsColors.textPrimary,
            inversePrimary = statsColors.glowRed,
            surfaceTint = statsColors.glowBlue
        )
    } else {
        lightColorScheme( // ✅ это тоже функция
            primary = statsColors.glowBlue,
            onPrimary = statsColors.textPrimary,
            primaryContainer = statsColors.cardBackground,
            onPrimaryContainer = statsColors.textPrimary,
            secondary = statsColors.glowRed,
            onSecondary = statsColors.textSecondary,
            secondaryContainer = statsColors.cardBackground,
            onSecondaryContainer = statsColors.textSecondary,
            tertiary = Color(0xFF00E676),
            onTertiary = Color.Black,
            tertiaryContainer = statsColors.cardBackground,
            onTertiaryContainer = statsColors.textPrimary,
            background = statsColors.background,
            onBackground = statsColors.textPrimary,
            surface = statsColors.cardBackground,
            onSurface = statsColors.textPrimary,
            surfaceVariant = statsColors.cardBackground,
            onSurfaceVariant = statsColors.textSecondary,
            error = Color(0xFFB00020),
            onError = Color.White,
            errorContainer = Color(0xFFCF6679),
            onErrorContainer = Color.Black,
            outline = statsColors.textSecondary,
            outlineVariant = statsColors.textSecondary.copy(alpha = 0.5f),
            scrim = Color.Black,
            inverseSurface = statsColors.background,
            inverseOnSurface = statsColors.textPrimary,
            inversePrimary = statsColors.glowRed,
            surfaceTint = statsColors.glowBlue
        )
    }
}

@Composable
fun QRMealTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val statsColors = NeonStatsColors // Можно потом подставлять динамически
    val colorScheme = toMaterialColorScheme(statsColors, darkTheme)

    CompositionLocalProvider(LocalStatsColors provides statsColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}