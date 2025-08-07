package com.example.qrmealtrack.presentation.trends.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.example.qrmealtrack.presentation.trends.model.UiChartPoint
import java.time.LocalDate

fun calculateNearestPoint(
    tapOffset: Offset,
    points: List<UiChartPoint>,
    canvasSize: Size,
    uniqueDates: List<LocalDate>,
    minValue: Float,
    maxValue: Float
): Pair<Int, Offset>? {
    if (points.isEmpty()) return null

    val spacing = canvasSize.width / (uniqueDates.size + 1)
    val heightRange = (maxValue - minValue).takeIf { it != 0f } ?: 1f

    var closestIndex = -1
    var closestOffset = Offset.Zero
    var minDistance = Float.MAX_VALUE

    points.forEachIndexed { i, point ->
        val index = uniqueDates.indexOf(point.rawDate)
        if (index == -1) return@forEachIndexed // защита

        val x = spacing * (index + 1)
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


