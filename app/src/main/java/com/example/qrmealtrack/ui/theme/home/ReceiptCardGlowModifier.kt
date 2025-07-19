package com.example.qrmealtrack.ui.theme.home


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.receiptCardGlowBackground(
    glowPrimary: Color = Color(0xFF00FFB0),
    glowSecondary: Color = Color(0xFF00D4FF),
    backgroundColor: Color = Color(0xCC121C2E),
    glowAlpha: Float = 0.5f,
    cornerRadius: Dp = 20.dp,
    borderWidth: Dp = 1.5.dp
): Modifier = this
    // Мягкая тень снизу → создаёт эффект “приподнятости”
    .drawBehind {
        val shadowColor = Color.Black.copy(alpha = 0.25f)
        val blurRadius = 8.dp.toPx()
        val offsetY = 6.dp.toPx()

        val shadowPaint = android.graphics.Paint().apply {
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
                cornerRadius.toPx(),
                cornerRadius.toPx(),
                shadowPaint
            )
        }
    }
    // Основной фон (градиент)
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                backgroundColor.copy(alpha = 0.95f),
                backgroundColor.copy(alpha = 0.7f)
            )
        ),
        shape = RoundedCornerShape(cornerRadius)
    )
    // Неоновая рамка с градиентом
    .border(
        width = borderWidth,
        brush = Brush.linearGradient(
            colors = listOf(
                glowPrimary.copy(alpha = glowAlpha),
                glowSecondary.copy(alpha = glowAlpha)
            ),
            start = Offset.Zero,
            end = Offset.Infinite
        ),
        shape = RoundedCornerShape(cornerRadius)
    )
    // Верхний стеклянный блик (делает карточку более объёмной)
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF00FFB0).copy(alpha = 0.05f), // светлый блик сверху
                Color.Transparent                // уходит в прозрачность
            ),
            startY = 0f,
            endY = 50f
        ),
        shape = RoundedCornerShape(cornerRadius)
    )



