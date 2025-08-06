package com.example.qrmealtrack.presentation.trends.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.qrmealtrack.presentation.trends.model.UiChartPoint

@Composable
fun TooltipBox(point: UiChartPoint, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0A0E1A))
            .border(1.dp, point.color, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = point.category,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${point.value.toInt()} MDL",
                color = point.color,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = point.dateLabel,
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}