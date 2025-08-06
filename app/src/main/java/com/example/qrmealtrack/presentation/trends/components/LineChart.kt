package com.example.qrmealtrack.presentation.trends.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.qrmealtrack.presentation.trends.model.UiChartPoint
import com.example.qrmealtrack.presentation.trends.util.calculateNearestPoint
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size

@Composable
fun LineChart(
    points: List<UiChartPoint>,
    modifier: Modifier = Modifier,
    selectedIndex: Int? = null,
    onPointTap: (Int) -> Unit
) {
    // Determine if scrolling is needed
    val needScroll = points.size > 20
    val scrollState = rememberScrollState()

    // Size of the canvas — set after layout
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    // Modifier for scrollable canvas
    val canvasModifier = if (needScroll) {
        modifier.horizontalScroll(scrollState)
    } else {
        modifier
    }

    Box(
        modifier = canvasModifier
            .pointerInput(points, canvasSize) {
                detectTapGestures { offset ->
                    val index = calculateNearestPoint(offset, points, canvasSize)
                    index?.let { onPointTap(it) }
                }
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .let {
                    if (needScroll) it.width((points.size * 60).dp) else it
                }
                .onSizeChanged { canvasSize = it.toSize() }
        ) {
            val spacing = size.width / (points.size + 1)
            val maxValue = points.maxOf { it.value }
            val minValue = points.minOf { it.value }
            val heightRange = maxValue - minValue

            // Map data points to screen coordinates
            val coordinates = points.mapIndexed { i, point ->
                val x = spacing * (i + 1)
                val y = size.height - (point.value - minValue) / heightRange * size.height
                Offset(x, y)
            }

            drawNeonGrid()

            // Draw connecting lines
            for (i in 0 until coordinates.lastIndex) {
                drawLine(
                    color = points[i].color,
                    start = coordinates[i],
                    end = coordinates[i + 1],
                    strokeWidth = 4f
                )
            }

            // Draw points
            coordinates.forEachIndexed { i, offset ->
                drawCircle(
                    color = if (i == selectedIndex) Color.Magenta else points[i].color,
                    radius = if (i == selectedIndex) 12f else 8f,
                    center = offset
                )
            }
        }

        // Tooltip above selected point
        selectedIndex?.let { index ->
            val point = points[index]
            TooltipBox(point, modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}


// рисуем сетку
fun DrawScope.drawNeonGrid() {
    val stepX = size.width / 6
    val stepY = size.height / 6
    for (i in 0..6) {
        drawLine(Color.Cyan.copy(alpha = 0.4f), Offset(i * stepX, 0f), Offset(i * stepX, size.height), 0.8f)
        drawLine(Color.Cyan.copy(alpha = 0.4f), Offset(0f, i * stepY), Offset(size.width, i * stepY), 0.8f)
    }
}