package com.example.qrmealtrack.presentation.trends.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

@Composable
fun Triangle(color: Color, pointingUp: Boolean) {
    Canvas(modifier = Modifier.size(12.dp, 6.dp)) {
        val path = Path().apply {
            if (pointingUp) {
                moveTo(0f, size.height)
                lineTo(size.width / 2, 0f)
                lineTo(size.width, size.height)
            } else {
                moveTo(0f, 0f)
                lineTo(size.width / 2, size.height)
                lineTo(size.width, 0f)
            }
            close()
        }
        drawPath(path, color)
    }
}