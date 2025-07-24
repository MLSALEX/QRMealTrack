package com.example.qrmealtrack.presentation.stats.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.qrmealtrack.presentation.stats.colors_provider.defaultCategoryColors
import com.example.qrmealtrack.presentation.stats.components.utils.PreviewMocks
import com.example.qrmealtrack.presentation.stats.model.CategoryUiModel
import com.example.qrmealtrack.presentation.stats.model.toUiModels

@Composable
fun DonutChartWithCenterButton(
    categories: List<CategoryUiModel>,
    labelMode: LabelMode,
    onModeChange: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 80.dp
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        DonutChart(
            categories = categories,
            modifier = Modifier.matchParentSize()
        )

        LabelModeButton(
            currentMode = labelMode,
            onModeChange = onModeChange,
            modifier = Modifier.size(buttonSize)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DonutChartWithButtonPreview() {
    val categoryColors = remember { defaultCategoryColors() }

    val (categoryUiModels, _) = PreviewMocks.categoryStats.toUiModels(
        colors = categoryColors,
        labelMode = LabelMode.BOTH
    )

    // Для превью просто фиксируем LabelMode (без смены)
    val labelMode = LabelMode.BOTH

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        DonutChartWithCenterButton(
            categories = categoryUiModels,
            labelMode = labelMode,
            onModeChange = {}, // в превью не нужен
            modifier = Modifier.size(250.dp)
        )
    }
}