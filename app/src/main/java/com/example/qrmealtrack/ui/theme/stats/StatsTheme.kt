package com.example.qrmealtrack.ui.theme.stats

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

// Определяем структуру цветов для экрана статистики
data class StatsColors(
    val background: Color,
    val cardBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val glowBlue: Color,
    val glowRed: Color,
)

// Готовый набор неоновых цветов
val NeonStatsColors = StatsColors(
    background = Color(		0xFF324759),
    cardBackground = Color(0xFF1A2E43),
    textPrimary = Color(0xFFE0F7FA),
    textSecondary = Color(0xFFB2EBF2),
    glowBlue = Color(0xFF00E5FF),
    glowRed = Color(	0xFFFFB3A7)
)

// CompositionLocal для передачи цветов внутрь
val LocalStatsColors = staticCompositionLocalOf { NeonStatsColors }
object StatsTheme {
    val colors: StatsColors
        @Composable get() = LocalStatsColors.current
}

@Composable
fun StatsTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalStatsColors provides NeonStatsColors) {
        content()
    }
}
