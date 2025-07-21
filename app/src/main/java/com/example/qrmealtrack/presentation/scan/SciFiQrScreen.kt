package com.example.qrmealtrack.presentation.scan

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp

@Composable
fun SciFiQrScreen() {
    val frameSizeDp = 200.dp
    val frameWidthPx = with(LocalDensity.current) { frameSizeDp.toPx() }

    // ✅ выносим анимацию за пределы Canvas (не пересоздается)
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = ""
    )

    // ✅ цвет glow обновляем через rememberUpdatedState
    val glowColor by rememberUpdatedState(Color(0x8000CFFF).copy(alpha = glowAlpha))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050A12))
    ) {
        // === Фон ===
        SciFiBackground(
            modifier = Modifier.fillMaxSize()
        )

        // === Верхняя сетка ===
        TopPerspectiveGrid(
            frameWidthPx = frameWidthPx,
            rows = 8,
            columns = 8,
            lineColor = Color(0xFF00CFFF),
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .align(Alignment.TopCenter)
        )

        // === Нижняя сетка ===
        BottomPerspectiveGrid(
            frameWidthPx = frameWidthPx,
            rows = 8,
            columns = 8,
            lineColor = Color(0xFF00CFFF),
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .align(Alignment.BottomCenter)
        )

        // === Рамка с glow ===
        QRFrameWithGlow(
            frameSizeDp = frameSizeDp,
            glowColor = glowColor,
            frameColor = Color(0xFF00CFFF),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun SciFiBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val gradient = Brush.radialGradient(
            colors = listOf(Color(0x2200CFFF), Color(0xFF050A12)),
            center = center,
            radius = size.minDimension / 1.5f
        )
        drawRect(brush = gradient, size = size)
    }
}
@Composable
fun TopPerspectiveGrid(
    frameWidthPx: Float,
    rows: Int,
    columns: Int,
    lineColor: Color,
    modifier: Modifier = Modifier
) {

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Верхняя сетка сужается вниз
        val yTop = h * 0.1f
        val yBottom = h * 0.6f

        val topLeft = 0f
        val topRight = w
        val bottomLeft = (w - frameWidthPx) / 2f
        val bottomRight = (w + frameWidthPx) / 2f

        drawPerspectiveGrid(
            yStart = yTop,
            yEnd = yBottom,
            topLeft = topLeft,
            topRight = topRight,
            bottomLeft = bottomLeft,
            bottomRight = bottomRight,
            rows = rows,
            columns = columns,
            color = lineColor
        )
    }
}

@Composable
fun BottomPerspectiveGrid(
    frameWidthPx: Float,
    rows: Int,
    columns: Int,
    lineColor: Color,
    modifier: Modifier = Modifier
) {

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Нижняя сетка сужается вверх
        val yBottom = h * 0.9f
        val yTop = h * 0.4f

        val topLeft = (w - frameWidthPx) / 2f
        val topRight = (w + frameWidthPx) / 2f
        val bottomLeft = 0f
        val bottomRight = w

        drawPerspectiveGrid(
            yStart = yBottom,
            yEnd = yTop,
            topLeft = bottomLeft,
            topRight = bottomRight,
            bottomLeft = topLeft,
            bottomRight = topRight,
            rows = rows,
            columns = columns,
            color = lineColor
        )
    }
}

@Composable
fun QRFrameWithGlow(
    frameSizeDp: Dp,
    glowColor: Color,
    frameColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(frameSizeDp)
            .drawBehind {
                drawRect(
                    color = glowColor,
                    size = size
                )
            }
    ) {
        QRFrame(
            modifier = Modifier.fillMaxSize(),
            frameColor = frameColor
        )
    }
}
@Composable
fun QRFrame(
    modifier: Modifier = Modifier,
    frameColor: Color = Color(0xFF00CFFF),
    cornerLength: Dp = 32.dp,
    strokeWidth: Dp = 4.dp
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cl = cornerLength.toPx()
        val sw = strokeWidth.toPx()

        drawLine(frameColor, Offset(0f, 0f), Offset(cl, 0f), sw)
        drawLine(frameColor, Offset(0f, 0f), Offset(0f, cl), sw)

        drawLine(frameColor, Offset(w, 0f), Offset(w - cl, 0f), sw)
        drawLine(frameColor, Offset(w, 0f), Offset(w, cl), sw)

        drawLine(frameColor, Offset(0f, h), Offset(cl, h), sw)
        drawLine(frameColor, Offset(0f, h), Offset(0f, h - cl), sw)

        drawLine(frameColor, Offset(w, h), Offset(w - cl, h), sw)
        drawLine(frameColor, Offset(w, h), Offset(w, h - cl), sw)
    }
}

private fun DrawScope.drawPerspectiveGrid(
    yStart: Float,
    yEnd: Float,
    topLeft: Float,
    topRight: Float,
    bottomLeft: Float,
    bottomRight: Float,
    rows: Int,
    columns: Int,
    color: Color
) {
    // Горизонтальные линии
    for (i in 0..rows) {
        val t = i / rows.toFloat()
        val y = lerp(yStart, yEnd, t)
        val leftX = lerp(topLeft, bottomLeft, t)
        val rightX = lerp(topRight, bottomRight, t)
        drawLine(
            color = color,
            start = Offset(leftX, y),
            end = Offset(rightX, y),
            strokeWidth = 1f
        )
    }

    // Вертикальные линии
    for (i in 0..columns) {
        val t = i / columns.toFloat()
        val xTop = lerp(topLeft, topRight, t)
        val xBottom = lerp(bottomLeft, bottomRight, t)
        drawLine(
            color = color,
            start = Offset(xTop, yStart),
            end = Offset(xBottom, yEnd),
            strokeWidth = 1f
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun SciFiQrScreenPreview() {
    SciFiQrScreen()
}