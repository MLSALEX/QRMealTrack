package com.example.qrmealtrack.presentation.stats.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DonutChartWithCenterButton(
    stats: List<CategoryStat>,
    modifier: Modifier = Modifier
) {
    var labelMode by remember { mutableStateOf(LabelMode.PERCENT) }
    val colors = rememberCategoryColors()

    // лямбда для смены режима – запоминаем, чтобы не пересоздавалась
    val onModeChange = remember {
        {
            labelMode = when (labelMode) {
                LabelMode.PERCENT -> LabelMode.NAME
                LabelMode.NAME -> LabelMode.BOTH
                LabelMode.BOTH -> LabelMode.PERCENT
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        DonutChart(
            stats = stats,
            colors = colors,
            strokeWidth = 350f,
            labelMode = labelMode,
            modifier = Modifier.size(250.dp)
        )

        LabelModeButton(
            currentMode = labelMode,
            onModeChange = onModeChange,
            modifier = Modifier
        )
    }
}
@Preview(showBackground = true)
@Composable
fun DonutChartWithButtonPreview() {
    val mockStats = remember {
        listOf(
            CategoryStat("NO_CATEGORY", 120.0),
            CategoryStat("MEALS", 300.0),
            CategoryStat("CLOTHING", 180.0),
            CategoryStat("BEAUTY", 90.0),
            CategoryStat("TRANSPORT", 150.0),
            CategoryStat("GROCERIES", 250.0)
        )
    }

    DonutChartWithCenterButton(
        stats = mockStats,
        modifier = Modifier.fillMaxSize()
    )
}