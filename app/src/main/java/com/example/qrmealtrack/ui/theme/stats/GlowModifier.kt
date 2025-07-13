package com.example.qrmealtrack.ui.theme.stats

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.nativeCanvas
import android.graphics.Paint as AndroidPaint

/**
 * Applies a diagonal glow gradient behind the content and border.
 */
fun Modifier.glassGlowBackground(
    glowRed: Color,
    glowBlue: Color,
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color,
    glowAlpha: Float = 0.5f,
    borderWidth: Dp = 1.5.dp
): Modifier = this
    .drawBehind {
        val shadowColor = Color.Black.copy(alpha = 0.15f) // мягкая настоящая тень
        val blurRadius = 3.dp.toPx() // радиус размытия
        val offsetY = 10.dp.toPx() // тень только снизу

        val paint = android.graphics.Paint().apply {
            isAntiAlias = true
            style = android.graphics.Paint.Style.FILL
            color = android.graphics.Color.TRANSPARENT
            setShadowLayer(
                blurRadius,
                0f,
                offsetY,
                shadowColor.toArgb()
            )
        }

        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawRoundRect(
                0f,
                0f,
                size.width,
                size.height,
                24.dp.toPx(),
                24.dp.toPx(),
                paint
            )
        }
    }
    // Неоновый градиент по краям
    .background(
        brush = Brush.linearGradient(
            colors = listOf(
                glowRed.copy(alpha = glowAlpha),
                Color.Transparent,
                glowBlue.copy(alpha = glowAlpha)
            ),
            start = Offset.Infinite,
            end = Offset.Zero
        ),
        shape = shape
    )
    // Светящаяся рамка
    .border(
        width = borderWidth,
        brush = Brush.linearGradient(
            colors = listOf(
                glowBlue.copy(alpha = glowAlpha),
                glowRed.copy(alpha = glowAlpha)
            ),
            start = Offset.Zero,
            end = Offset.Infinite
        ),
        shape = shape
    )
    // Стеклянный верхний градиент
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF26C6DA).copy(alpha = 0.02f),
                Color(0xFF7C4DFF).copy(alpha = 0.01f)
            )
        ),
        shape = shape
    )



