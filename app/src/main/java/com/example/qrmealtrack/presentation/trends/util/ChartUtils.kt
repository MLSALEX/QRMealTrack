package com.example.qrmealtrack.presentation.trends.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.example.qrmealtrack.presentation.trends.model.UiChartPoint
import kotlin.math.abs

fun calculateNearestPoint(
    tapOffset: Offset,
    points: List<UiChartPoint>,
    canvasSize: Size
): Int? {
    if (points.isEmpty()) return null
    val spacing = canvasSize.width / (points.size + 1)
    val xs = points.indices.map { spacing * (it + 1) }
    val nearestIndex = xs.indices.minByOrNull { abs(xs[it] - tapOffset.x) } ?: return null
    return nearestIndex
}