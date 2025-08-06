package com.example.qrmealtrack.presentation.trends.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.example.qrmealtrack.presentation.trends.model.UiChartPoint

fun calculateNearestPoint(
    tapOffset: Offset,
    points: List<UiChartPoint>,
    canvasSize: Size
): Pair<Int, Offset>? {
    if (points.isEmpty()) return null

    val spacing = canvasSize.width / (points.size + 1)
    val maxValue = points.maxOf { it.value }
    val minValue = points.minOf { it.value }
    val heightRange = maxValue - minValue

    var closestIndex = -1
    var closestOffset = Offset.Zero
    var minDistance = Float.MAX_VALUE

    points.forEachIndexed { i, point ->
        val x = spacing * (i + 1)
        val y = canvasSize.height - (point.value - minValue) / heightRange * canvasSize.height
        val pointOffset = Offset(x, y)
        val distance = (tapOffset - pointOffset).getDistance()

        if (distance < minDistance) {
            closestIndex = i
            closestOffset = pointOffset
            minDistance = distance
        }
    }

    return if (closestIndex != -1) closestIndex to closestOffset else null
}


