package com.example.qrmealtrack.presentation.trends.state

import com.example.qrmealtrack.presentation.components.FilterType
import com.example.qrmealtrack.presentation.trends.components.GranularityType
import com.example.qrmealtrack.presentation.trends.model.UiChartPoint

data class TrendsUiState(
    val filter: FilterType.Categories,
    val granularity: GranularityType,
    val groupedPoints: Map<String, List<UiChartPoint>>
)