package com.example.qrmealtrack.presentation.trends.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


enum class GranularityType { DAY, WEEK, MONTH }

@Composable
fun NeonSegmentedButton(
    selected: GranularityType,
    onGranularityChange: (GranularityType) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = GranularityType.entries

    Row(
        modifier = modifier
            .wrapContentWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0D111A))
            .border(1.dp, Color.Cyan.copy(alpha = 0.2f), RoundedCornerShape(10.dp)) // подчёркиваем контейнер
            .padding(2.dp)
    ) {
        options.forEachIndexed { index, type ->
            val isSelected = type == selected

            Button(
                onClick = { onGranularityChange(type) },
                modifier = Modifier
                    .height(32.dp)
                    .clip(RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color.Cyan.copy(alpha = 0.15f) else Color.Transparent,
                    contentColor = if (isSelected) Color.White else Color.Cyan.copy(alpha = 0.6f)
                ),
                border = if (isSelected) BorderStroke(0.5.dp, Color.Cyan) else null
            ) {
                Text(
                    text = type.name,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF0B0F18)
@Composable
fun NeonSegmentedButtonPreview() {
    var selected by remember { mutableStateOf(GranularityType.WEEK) }

    NeonSegmentedButton(
        selected = selected,
        onGranularityChange = { selected = it },
        modifier = Modifier.padding(16.dp)
    )
}