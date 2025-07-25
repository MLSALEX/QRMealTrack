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
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp

object SciFiTheme {
    val background = Color(0xFF050A12)
    val gridLine = Color(0xFF00CFFF)
    val gridGlow = Color.White.copy(alpha = 0.4f)
    val frameGlowBase = Color(0x8000CFFF)
    val frameHighlight = Color(0xFF00CFFF)
    val holeTint = Color(0x3300CFFF)

    val frameCornerRadius = 12.dp
    val frameSize = 160.dp
    val gridPadding = 90.dp
    val gridHeight = 100.dp
    const val gridRows = 8
    const val gridColumns = 8
}

@Composable
fun SciFiQrScreen() {
    val density = LocalDensity.current
    val cornerRadiusPx = with(density) { SciFiTheme.frameCornerRadius.toPx() }
    val gridPaddingPx = with(density) { SciFiTheme.gridPadding.toPx() }
    val gridHeightPx = with(density) { SciFiTheme.gridHeight.toPx() }
    val frameWidthPx = with(density) { SciFiTheme.frameSize.toPx() }

    // 🔥 Transition 1 – ТОЛЬКО для сетки
    val pulseTransition = rememberInfiniteTransition(label = "GridPulse")
    val pulse by pulseTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // 🔥 Transition 2 – для рамки (glowAlpha + бегущий свет вместе)
    val frameTransition = rememberInfiniteTransition(label = "FrameRunner")
    val glowAlpha by frameTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    val frameProgress by frameTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(4000, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "frameProgress"
    )

    val frameGlowColor = SciFiTheme.frameGlowBase.copy(alpha = glowAlpha)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SciFiTheme.background),
        contentAlignment = Alignment.Center
    ) {
        // ✅ 1. Фон рисуется один раз и не перерисовывается
        SciFiBackgroundWithHole(
            holeSizeDp = SciFiTheme.frameSize,
            cornerRadius = cornerRadiusPx,
            tintColor = SciFiTheme.holeTint
        )

        // ✅ 2. Сетка обновляется ТОЛЬКО по pulse
        DoublePerspectiveGrid(
            paddingPx = gridPaddingPx,
            gridHeightPx = gridHeightPx,
            frameWidthPx = frameWidthPx,
            pulse = pulse
        )

        // ✅ 3. Рамка обновляется ТОЛЬКО по frameProgress + glowAlpha
        NeonTripleQRFrame(
            frameSizeDp = SciFiTheme.frameSize,
            frameColor = frameGlowColor,
            glowColor = SciFiTheme.frameHighlight,
            frameProgress = frameProgress,
            cornerRadiusPx = cornerRadiusPx
        )
    }
}

@Composable
fun SciFiBackgroundWithHole(
    holeSizeDp: Dp,
    cornerRadius: Float,
    tintColor: Color
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val holeSizePx = holeSizeDp.toPx()

        val gradient = Brush.radialGradient(
            colors = listOf(SciFiTheme.frameGlowBase, SciFiTheme.background),
            center = center,
            radius = size.minDimension / 1.9f
        )
        drawRect(brush = gradient, size = size)

        val left = (w - holeSizePx) / 2
        val top = (h - holeSizePx) / 2
        val holeSize = Size(holeSizePx, holeSizePx)

        drawRoundRect(
            color = Color.Transparent,
            topLeft = Offset(left, top),
            size = holeSize,
            cornerRadius = CornerRadius(cornerRadius, cornerRadius),
            blendMode = BlendMode.Clear
        )
        drawRoundRect(
            color = tintColor,
            topLeft = Offset(left, top),
            size = holeSize,
            cornerRadius = CornerRadius(cornerRadius, cornerRadius),
            alpha = 1f
        )
    }
}

@Composable
fun DoublePerspectiveGrid(
    paddingPx: Float,
    gridHeightPx: Float,
    frameWidthPx: Float,
    pulse: Float // <-- управляется снаружи
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Верхняя сетка (сужается вниз)
        drawPerspectiveGridWithGlow(
            yStart = paddingPx,
            yEnd = paddingPx + gridHeightPx,
            topLeft = 0f,
            topRight = w,
            bottomLeft = (w - frameWidthPx) / 2f,
            bottomRight = (w + frameWidthPx) / 2f,
            rows = SciFiTheme.gridRows,
            columns = SciFiTheme.gridColumns,
            baseColor = SciFiTheme.gridLine,
            pulse = pulse
        )

        // Нижняя сетка (расширяется вверх)
        drawPerspectiveGridWithGlow(
            yStart = h - paddingPx,
            yEnd = h - paddingPx - gridHeightPx,
            topLeft = 0f,
            topRight = w,
            bottomLeft = (w - frameWidthPx) / 2f,
            bottomRight = (w + frameWidthPx) / 2f,
            rows = SciFiTheme.gridRows,
            columns = SciFiTheme.gridColumns,
            baseColor = SciFiTheme.gridLine,
            pulse = pulse
        )
    }
}

// === Glow-эффект + линии сетки ===
private fun DrawScope.drawPerspectiveGridWithGlow(
    yStart: Float,
    yEnd: Float,
    topLeft: Float,
    topRight: Float,
    bottomLeft: Float,
    bottomRight: Float,
    rows: Int,
    columns: Int,
    baseColor: Color,
    pulse: Float
) {
    val glowAlpha = lerp(0.1f, 0.7f, pulse)
    val glowBlur = lerp(1f, 16f, pulse)
    val glowStroke = lerp(1f, 10f, pulse)

    for (i in 0..columns) {
        val t = i / columns.toFloat()
        val xTop = lerp(topLeft, topRight, t)
        val xBottom = lerp(bottomLeft, bottomRight, t)

        // Мягкий размытый ореол
        drawBlurredLine(
            color = baseColor.copy(alpha = glowAlpha),
            start = Offset(xTop, yStart),
            end = Offset(xBottom, yEnd),
            blurRadius = glowBlur,
            strokeWidth = glowStroke
        )

        // Ядро линии (свет + белая подсветка)
        drawLine(
            color = baseColor,
            start = Offset(xTop, yStart),
            end = Offset(xBottom, yEnd),
            strokeWidth = 1.5f
        )
        drawLine(
            color = SciFiTheme.gridGlow,
            start = Offset(xTop, yStart),
            end = Offset(xBottom, yEnd),
            strokeWidth = 0.8f
        )
    }

    // Горизонтали (без glow, чтобы не перегружать)
    for (i in 0..rows) {
        val t = i / rows.toFloat()
        val y = lerp(yStart, yEnd, t)
        val leftX = lerp(topLeft, bottomLeft, t)
        val rightX = lerp(topRight, bottomRight, t)

        drawLine(
            color = baseColor.copy(alpha = 0.5f),
            start = Offset(leftX, y),
            end = Offset(rightX, y),
            strokeWidth = 1f
        )
    }
}

// === Свечение линии с blur ===
private fun DrawScope.drawBlurredLine(
    start: Offset,
    end: Offset,
    color: Color,
    blurRadius: Float,
    strokeWidth: Float
) {
    drawIntoCanvas { canvas ->
        val paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            style = android.graphics.Paint.Style.STROKE
            this.color = color.toArgb()
            this.strokeWidth = strokeWidth
            maskFilter = android.graphics.BlurMaskFilter(
                blurRadius,
                android.graphics.BlurMaskFilter.Blur.NORMAL
            )
        }
        canvas.nativeCanvas.drawLine(start.x, start.y, end.x, end.y, paint)
    }
}

@Composable
fun NeonTripleQRFrame(
    frameSizeDp: Dp,
    frameColor: Color,
    glowColor: Color,
    frameProgress: Float,
    cornerRadiusPx: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(frameSizeDp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidths = listOf(4f, 4f, 4f)
            val paddingStep = 10.dp.toPx()
            val totalPerimeter = 2 * (size.width + size.height)

            strokeWidths.forEachIndexed { index, strokeWidth ->
                val inset = index * paddingStep

                // Статическая рамка
                drawRoundRect(
                    color = frameColor,
                    topLeft = Offset(inset, inset),
                    size = Size(size.width - inset * 2, size.height - inset * 2),
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                    style = Stroke(width = strokeWidth)
                )

                // Бегущий свет (по frameProgress)
                val roundRect = RoundRect(
                    left = inset,
                    top = inset,
                    right = size.width - inset,
                    bottom = size.height - inset,
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )
                val path = Path().apply { addRoundRect(roundRect) }

                val lightLength = totalPerimeter * 0.15f
                val lightStart = totalPerimeter * frameProgress
                val lightEnd = (lightStart + lightLength).coerceAtMost(totalPerimeter)

                drawPathSegmentGlow(
                    path = path,
                    start = lightStart,
                    end = lightEnd,
                    totalLength = totalPerimeter,
                    glowColor = glowColor,
                    strokeWidth = strokeWidth + 6f
                )
            }
        }
    }
}

// === Подсветка для бегущего света ===
fun DrawScope.drawPathSegmentGlow(
    path: Path,
    start: Float,
    end: Float,
    totalLength: Float,
    glowColor: Color,
    strokeWidth: Float
) {
    val visibleSegment = (end - start).coerceAtLeast(1f)
    val effect = PathEffect.dashPathEffect(
        intervals = floatArrayOf(start, visibleSegment, totalLength - end),
        phase = 0f
    )
    drawPath(
        path = path,
        color = glowColor.copy(alpha = 0.8f),
        style = Stroke(width = strokeWidth, pathEffect = effect)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun SciFiQrScreenPreview() {
    SciFiQrScreen()
}