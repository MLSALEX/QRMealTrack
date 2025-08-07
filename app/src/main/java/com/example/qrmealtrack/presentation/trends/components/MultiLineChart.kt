package com.example.qrmealtrack.presentation.trends.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.qrmealtrack.presentation.trends.model.UiChartPoint
import com.example.qrmealtrack.presentation.trends.util.calculateNearestPoint
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.flatten
import kotlin.collections.forEach
import kotlin.math.roundToInt

@Composable
fun MultiLineChart(
    groupedPoints: Map<String, List<UiChartPoint>>,
    selectedCategory: String?,
    selectedIndex: Int?,
    selectedOffset: Offset?,
    onPointTap: (String, Int, Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    val allPoints = groupedPoints.values.flatten()
    val needScroll = allPoints.size > 20
    val scrollState = rememberScrollState()

    var canvasSize by remember { mutableStateOf(Size.Zero) }

    val canvasModifier = if (needScroll) {
        modifier
            .horizontalScroll(scrollState)
            .onSizeChanged { canvasSize = it.toSize() }
    } else {
        modifier.onSizeChanged { canvasSize = it.toSize() }
    }

    Box(
        modifier = canvasModifier
            .pointerInput(groupedPoints, canvasSize) {
                detectTapGestures { offset ->
                    var closestCategory: String? = null
                    var closestIndex: Int? = null
                    var closestPointOffset: Offset? = null
                    var minDistance = Float.MAX_VALUE

                    groupedPoints.forEach { (category, points) ->
                        val result = calculateNearestPoint(offset, points, canvasSize)
                        if (result != null) {
                            val (index, pointOffset) = result
                            val distance = (offset - pointOffset).getDistance()
                            if (distance < minDistance) {
                                minDistance = distance
                                closestCategory = category
                                closestIndex = index
                                closestPointOffset = pointOffset
                            }
                        }
                    }

                    val category = closestCategory
                    val index = closestIndex
                    val offset = closestPointOffset

                    if (category != null && index != null && offset != null) {
                        onPointTap(category, index, offset)
                    }
                }
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .let {
                    if (needScroll) it.width((allPoints.size * 60).dp) else it
                }
        ) {
            val horizontalCells = allPoints.size + 1
            drawNeonGrid(horizontalCells = horizontalCells)
        }

        groupedPoints.forEach { (category, points) ->
            LineChart(
                points = points,
                selectedIndex = if (category == selectedCategory) selectedIndex else null,
                modifier = Modifier.matchParentSize()
            )
        }

        // ✅ Рисуем тултип поверх всего
        if (selectedCategory != null && selectedIndex != null && selectedOffset != null) {
            val selectedPoint = groupedPoints[selectedCategory]?.getOrNull(selectedIndex)
            if (selectedPoint != null) {
                val tooltipWidth = 120
                val tooltipHeight = 250
                val x = selectedOffset.x.roundToInt()
                val y = selectedOffset.y.roundToInt()

                val tooltipAbove = selectedOffset.y > canvasSize.height * 0.7f

                val xOffset = (x - tooltipWidth / 2).coerceIn(0, (canvasSize.width - tooltipWidth).toInt())
                val yOffset = if (tooltipAbove) {
                    (y - tooltipHeight).coerceAtLeast(0)
                } else {
                    (y + 16).coerceAtMost((canvasSize.height - tooltipHeight).toInt())
                }

                TooltipBox(
                    point = selectedPoint,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset { IntOffset(xOffset, yOffset) }
                )
            }
        }
    }
}

fun DrawScope.drawNeonGrid(
    horizontalCells: Int,
    verticalCells: Int = 6,
    color: Color = Color.Cyan.copy(alpha = 0.3f)
) {
    val cellWidth = size.width / horizontalCells
    val cellHeight = size.height / verticalCells

    for (i in 0..horizontalCells) {
        val x = i * cellWidth
        drawLine(
            color = color,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1f
        )
    }

    for (j in 0..verticalCells) {
        val y = j * cellHeight
        drawLine(
            color = color,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f
        )
    }
}

