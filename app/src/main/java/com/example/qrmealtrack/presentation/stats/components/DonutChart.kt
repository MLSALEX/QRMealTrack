package com.example.qrmealtrack.presentation.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DonutChart(
    stats: List<CategoryStat>,
    colors: List<Color>,
    labelMode: LabelMode,
    modifier: Modifier = Modifier
) {
    val total =  remember(stats) {stats.sumOf { it.total }}
    val currentStats = rememberUpdatedState(stats)
    val currentMode = rememberUpdatedState(labelMode)

    Canvas(modifier = modifier) {
        val safeStats = currentStats.value
        val mode = currentMode.value
        var startAngle = -90f
        val radius = size.minDimension / 2
        val dynamicStroke = size.minDimension * 0.5f

        safeStats.forEachIndexed { index, stat ->
            val sweepAngle = (stat.total / total * 360f).toFloat()
            drawArc(
                color = colors.getOrElse(index) { Color.Gray },
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = dynamicStroke)
            )
            // вычисляем позицию текста на середине дуги
            val middleAngle = startAngle + sweepAngle / 2
            val angleInRad = Math.toRadians((startAngle + sweepAngle / 2).toDouble())

            val textRadius = radius - dynamicStroke * 0.01f
            val textX = center.x + textRadius * cos(angleInRad).toFloat()
            val textY = center.y + textRadius * sin(angleInRad).toFloat()

            // генерируем текст в зависимости от режима
            val percent = (stat.total / total * 100).toInt()
            val text = when (mode) {
                LabelMode.PERCENT -> "$percent%"
                LabelMode.NAME -> stat.category
                LabelMode.BOTH -> "$percent% ${stat.category}"
            }
            // Автоповорот, чтобы текст не был вверх ногами
            val adjustedAngle = if (middleAngle > 90f && middleAngle < 270f) {
                middleAngle + 180f // переворачиваем
            } else {
                middleAngle
            }

            // рисуем текст
            drawContext.canvas.nativeCanvas.apply {
                save() // сохраняем состояние канваса
                translate(textX, textY)  // переносим систему координат в точку на дуге
                rotate(adjustedAngle)      // поворачиваем по направлению радиуса
                drawText(
                    text,
                    0f, 0f,  // теперь рисуем в "локальных" координатах
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = size.minDimension * 0.06f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
                restore() // возвращаем систему координат назад
            }
            startAngle += sweepAngle
        }
    }
}

@Immutable
data class CategoryStat(
    val category: String,
    val total: Double
)

@Composable
fun rememberCategoryColors(): List<Color> = remember {
    listOf(
        Color.Gray,        // NO_CATEGORY
        Color(0xFF2196F3), // MEALS
        Color(0xFF9C27B0), // CLOTHING
        Color(0xFFE91E63), // BEAUTY
        Color(0xFFFF9800), // TRANSPORT
        Color(0xFF4CAF50)  // GROCERIES
    )
}
@Preview(showBackground = true)
@Composable
fun DonutChartPreview() {
    // Моковые данные
    val mockStats = listOf(
        CategoryStat("NO_CATEGORY", 120.0),
        CategoryStat("MEALS", 300.0),
        CategoryStat("CLOTHING", 180.0),
        CategoryStat("BEAUTY", 90.0),
        CategoryStat("TRANSPORT", 150.0),
        CategoryStat("GROCERIES", 250.0)
    )
    val colors = rememberCategoryColors()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        DonutChart(
            stats = mockStats,
            colors = colors,
            modifier = Modifier.size(250.dp),
            labelMode = LabelMode.BOTH
        )
    }
}
