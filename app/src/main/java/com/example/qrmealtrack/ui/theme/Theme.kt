package com.example.qrmealtrack.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.example.qrmealtrack.ui.theme.stats.LocalStatsColors
import com.example.qrmealtrack.ui.theme.stats.NeonStatsColors

private val NeonDarkColorScheme = darkColorScheme(
    // Базовые ключевые цвета
    primary = NeonStatsColors.glowBlue,         // Основной акцент
    onPrimary = Color.Black,                    // Текст/иконки на акценте

    primaryContainer = NeonStatsColors.cardBackground, // Контейнеры с акцентом
    onPrimaryContainer = NeonStatsColors.textPrimary,

    secondary = NeonStatsColors.glowRed,        // Вторичный акцент
    onSecondary = Color.Black,
    secondaryContainer = NeonStatsColors.cardBackground,
    onSecondaryContainer = NeonStatsColors.textSecondary,

    tertiary = Color(0xFF00E676),               // Дополнительный акцент (зелёный)
    onTertiary = Color.Black,
    tertiaryContainer = NeonStatsColors.cardBackground,
    onTertiaryContainer = NeonStatsColors.textPrimary,

    // Фоновые слои
    background = NeonStatsColors.background,    // Главный фон
    onBackground = NeonStatsColors.textPrimary, // Текст/иконки на фоне

    surface = NeonStatsColors.cardBackground,   // Поверхности: карточки, bottom sheet
    onSurface = NeonStatsColors.textPrimary,

    surfaceVariant = NeonStatsColors.cardBackground, // Вариант поверхности (Card)
    onSurfaceVariant = NeonStatsColors.textSecondary,

    // Ошибки
    error = Color(0xFFFF5370),
    onError = Color.Black,
    errorContainer = Color(0xFFB00020),
    onErrorContainer = Color.White,

    // Контуры и overlay
    outline = NeonStatsColors.textSecondary.copy(alpha = 0.6f),
    outlineVariant = NeonStatsColors.textSecondary.copy(alpha = 0.3f),

    scrim = Color.Black.copy(alpha = 0.7f),     // затемнение под диалогами

    // Инверсии (для Snackbar/диалогов)
    inverseSurface = NeonStatsColors.background,
    inverseOnSurface = NeonStatsColors.textPrimary,
    inversePrimary = NeonStatsColors.glowRed,

    // Важно: отключаем tint, чтобы Card не накладывала overlay-тон
    surfaceTint = Color.Unspecified
)

@Composable
fun QRMealTrackTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = NeonDarkColorScheme

    CompositionLocalProvider(LocalStatsColors provides NeonStatsColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
