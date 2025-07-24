package com.example.qrmealtrack.presentation.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class LabelMode {
    PERCENT,        // только %
    NAME,           // только название
    BOTH            // и % и название
}

@Composable
fun LabelModeButton(
    currentMode: LabelMode,
    onModeChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    // текст для текущего режима
    val modeText = when (currentMode) {
        LabelMode.PERCENT -> "%"
        LabelMode.NAME -> "Name"
        LabelMode.BOTH -> "% + Name"
    }

    Box(
        modifier = modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(Color.LightGray, shape = CircleShape)
            .clickable { onModeChange() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(modeText, style = MaterialTheme.typography.bodyMedium)
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Change mode")
        }
    }
}