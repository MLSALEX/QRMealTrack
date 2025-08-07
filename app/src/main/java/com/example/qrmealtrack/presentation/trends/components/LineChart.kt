package com.example.qrmealtrack.presentation.trends.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.qrmealtrack.presentation.trends.model.UiChartPoint
import java.time.LocalDate

@Composable
fun LineChart(
    points: List<UiChartPoint>,
    uniqueDates: List<LocalDate>,
    minValue: Float,
    maxValue: Float,
    selectedIndex: Int? = null,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (points.isEmpty()) return@Canvas

            val spacing = size.width / (uniqueDates.size + 1)
            val heightRange = (maxValue - minValue).takeIf { it != 0f } ?: 1f

            val coordinates = points.map { point ->
                val index = uniqueDates.indexOf(point.rawDate)
                val x = spacing * (index + 1)
                val y = size.height - (point.value - minValue) / heightRange * size.height
                Offset(x, y)
            }

            for (i in 0 until coordinates.lastIndex) {
                drawLine(
                    color = points[i].color,
                    start = coordinates[i],
                    end = coordinates[i + 1],
                    strokeWidth = 4f
                )
            }

            coordinates.forEachIndexed { i, offset ->
                drawCircle(
                    color = if (i == selectedIndex) Color.Magenta else points[i].color,
                    radius = if (i == selectedIndex) 12f else 8f,
                    center = offset
                )
            }
        }
    }
}



