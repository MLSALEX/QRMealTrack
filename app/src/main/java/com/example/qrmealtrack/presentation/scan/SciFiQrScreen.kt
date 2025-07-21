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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp

@Composable
fun SciFiQrScreen() {
    val gridPaddingDp = 90.dp
    val gridPaddingPx = with(LocalDensity.current) { gridPaddingDp.toPx() }
    val frameSizeDp = 180.dp
    val frameWidthPx = with(LocalDensity.current) { frameSizeDp.toPx() }
    val gridHeightPx = with(LocalDensity.current) { 100.dp.toPx() }

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
        SciFiBackgroundWithHole(holeSizeDp = frameSizeDp)

        DoublePerspectiveGrid(
            frameWidthPx = frameWidthPx,
            topGridHeightPx = gridHeightPx,
            bottomGridHeightPx = gridHeightPx,
            rows = 8,
            columns = 8,
            lineColor = Color(0xFF00CFFF),
            modifier = Modifier.fillMaxSize(),
            paddingPx = gridPaddingPx,
        )
        NeonTripleQRFrame(
            frameSizeDp = frameSizeDp,
            frameColor = glowColor,
            glowColor = Color(0xFF00CFFF),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun SciFiBackgroundWithHole(
    holeSizeDp: Dp
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val holeSizePx = holeSizeDp.toPx()

        // Основной фон с sci-fi градиентом
        val gradient = Brush.radialGradient(
            colors = listOf(Color(0x2200CFFF), Color(0xFF050A12)),
            center = center,
            radius = size.minDimension / 1.5f
        )
        drawRect(brush = gradient, size = size)

        // Координаты окна
        val left = (w - holeSizePx) / 2
        val top = (h - holeSizePx) / 2
        val holeSize = Size(holeSizePx, holeSizePx)
        val cornerRadius = 30.dp.toPx()

        // ✅ 1. Вырезаем реальное окно → камера видна
        drawRoundRect(
            color = Color.Transparent,
            topLeft = Offset(left, top),
            size = holeSize,
            cornerRadius = CornerRadius(cornerRadius, cornerRadius),
            blendMode = BlendMode.Clear
        )

        // ✅ 2. Накладываем сплошной оттенок поверх окна (камера будет видна, но с тоном)
        drawRoundRect(
            color = Color(0x3300CFFF), // лёгкий циан оттенок
            topLeft = Offset(left, top),
            size = holeSize,
            cornerRadius = CornerRadius(cornerRadius, cornerRadius),
            alpha = 1f // прозрачность, камера не перекрыта
        )
    }
}
@Composable
fun DoublePerspectiveGrid(
    frameWidthPx: Float,
    topGridHeightPx: Float,
    bottomGridHeightPx: Float,
    paddingPx: Float,
    rows: Int,
    columns: Int,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // === ВЕРХНЯЯ СЕТКА (от самого верха до заданной высоты topGridHeightPx)
        val top_yStart = paddingPx
        val top_yEnd = paddingPx + topGridHeightPx

        val top_topLeft = 0f
        val top_topRight = w
        val top_bottomLeft = (w - frameWidthPx) / 2f
        val top_bottomRight = (w + frameWidthPx) / 2f

        drawPerspectiveGrid(
            yStart = top_yStart,
            yEnd = top_yEnd,
            topLeft = top_topLeft,
            topRight = top_topRight,
            bottomLeft = top_bottomLeft,
            bottomRight = top_bottomRight,
            rows = rows,
            columns = columns,
            color = lineColor
        )

        // === НИЖНЯЯ СЕТКА (от самого низа до заданной высоты bottomGridHeightPx)
        val bottom_yStart = h - paddingPx                 // низ экрана
        val bottom_yEnd = h - paddingPx - bottomGridHeightPx  // на сколько вверх она поднимается

        val bottom_topLeft = (w - frameWidthPx) / 2f
        val bottom_topRight = (w + frameWidthPx) / 2f
        val bottom_bottomLeft = 0f
        val bottom_bottomRight = w

        drawPerspectiveGrid(
            yStart = bottom_yStart,
            yEnd = bottom_yEnd,
            topLeft = bottom_bottomLeft,
            topRight = bottom_bottomRight,
            bottomLeft = bottom_topLeft,
            bottomRight = bottom_topRight,
            rows = rows,
            columns = columns,
            color = lineColor
        )
    }
}


@Composable
fun NeonTripleQRFrame(
    frameSizeDp: Dp,
    frameColor: Color,
    glowColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 4000, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "progress"
    )

    Box(
        modifier = modifier.size(frameSizeDp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidths = listOf(4f, 8f, 12f) // три слоя
            val paddingStep = 10.dp.toPx()
            val cornerRadius = 30.dp.toPx()

            val totalPerimeter = 2 * (size.width + size.height)

            strokeWidths.forEachIndexed { index, strokeWidth ->
                val inset = index * paddingStep

                // ===== СПЛОШНАЯ ЛИНИЯ =====
                drawRoundRect(
                    color = frameColor,
                    topLeft = Offset(inset, inset),
                    size = Size(size.width - inset * 2, size.height - inset * 2),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Stroke(width = strokeWidth)
                )

                // ===== ПУТЬ ДЛЯ БЕГУЩЕГО СВЕТА =====
                val roundRect = RoundRect(
                    left = inset,
                    top = inset,
                    right = size.width - inset,
                    bottom = size.height - inset,
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )

                val path = Path().apply {
                    addRoundRect(roundRect) // ✅ корректно добавляем форму
                }

                // Длина светящегося сегмента (~15% периметра)
                val lightLength = totalPerimeter * 0.15f
                val lightStart = totalPerimeter * animatedProgress
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