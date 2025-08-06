package com.example.qrmealtrack.presentation.trends.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.qrmealtrack.presentation.trends.model.UiChartPoint

@Composable
fun MultiLineChart(
    groupedPoints: Map<String, List<UiChartPoint>>,
    selectedIndex: Int?,
    onPointTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        groupedPoints.forEach { (_, points) ->
            LineChart(
                points = points,
                selectedIndex = selectedIndex,
                onPointTap = onPointTap,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}