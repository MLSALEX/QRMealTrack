package com.example.qrmealtrack.presentation.stats.components.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawArcSegment(
    color: Color,
    startAngle: Float,
    sweepAngle: Float,
    strokeWidth: Float
) {
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        style = Stroke(width = strokeWidth)
    )
}

fun drawLabelOnArc(
    canvas: android.graphics.Canvas,
    center: Offset,
    radius: Float,
    startAngle: Float,
    sweepAngle: Float,
    labelText: String,
    textPaint: android.graphics.Paint,
    stroke: Float
) {
    val middle = middleAngle(startAngle, sweepAngle)

    val textRadius = radius - stroke * 0.01f
    val pos = polarToCartesian(center, textRadius, middle)

    val adjustedAngle = adjustTextAngle(middle)

    canvas.apply {
        save()
        translate(pos.x, pos.y)
        rotate(adjustedAngle)
        drawText(labelText, 0f, 0f, textPaint)
        restore()
    }
}

/**
 * Перевод полярных координат в декартовые (x, y)
 */
fun polarToCartesian(
    center: Offset,
    radius: Float,
    angleDegrees: Float
): Offset {
    val angleRad = Math.toRadians(angleDegrees.toDouble())
    val x = center.x + radius * cos(angleRad).toFloat()
    val y = center.y + radius * sin(angleRad).toFloat()
    return Offset(x, y)
}

/**
 * Вычисляет средний угол дуги
 */
fun middleAngle(startAngle: Float, sweepAngle: Float): Float {
    return startAngle + sweepAngle / 2
}

/**
 * Корректирует угол, если текст "вниз головой"
 */
fun adjustTextAngle(angle: Float): Float {
    return if (angle > 90f && angle < 270f) angle + 180f else angle
}
