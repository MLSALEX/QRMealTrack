package com.example.qrmealtrack.presentation.components

import androidx.compose.foundation.ScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.verticalColumnScrollbar(
    scrollState: ScrollState,
    width: Dp = 4.dp,
    scrollBarHeight: Dp = 100.dp,
    endPadding: Dp = 8.dp,
    cornerRadius: Dp = 8.dp
): Modifier = composed {
    val trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
    val thumbColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)

    drawWithContent {
        drawContent()

        val containerHeight = size.height
        val scrollOffset =
            (scrollState.value / (scrollState.maxValue.toFloat().coerceAtLeast(1f))) *
                    (containerHeight - scrollBarHeight.toPx())

        drawRoundRect(
            color = trackColor,
            topLeft = Offset(x = size.width - width.toPx() - endPadding.toPx(), y = 0f),
            size = Size(width.toPx(), containerHeight),
            cornerRadius = CornerRadius(cornerRadius.toPx())
        )

        drawRoundRect(
            color = thumbColor,
            topLeft = Offset(x = size.width - width.toPx() - endPadding.toPx(), y = scrollOffset),
            size = Size(width.toPx(), scrollBarHeight.toPx()),
            cornerRadius = CornerRadius(cornerRadius.toPx())
        )
    }
}