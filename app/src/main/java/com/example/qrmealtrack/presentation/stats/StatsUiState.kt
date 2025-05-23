package com.example.qrmealtrack.presentation.stats

import com.example.qrmealtrack.domain.model.PriceChangeItem
import com.example.qrmealtrack.domain.usecase.StatsSummary

data class StatsUiState(
    val summary: StatsSummary = StatsSummary(0.0, 0.0, null, 0.0, 0),
    val priceDynamics: List<PriceChangeItem> = emptyList(),
    val selectedFilter: TimeFilter = TimeFilter.Week
)