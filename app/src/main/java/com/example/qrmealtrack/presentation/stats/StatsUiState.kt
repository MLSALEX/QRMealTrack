package com.example.qrmealtrack.presentation.stats

import com.example.qrmealtrack.domain.model.PriceChangeItem
import com.example.qrmealtrack.domain.usecase.StatsSummary

data class StatsUiState(
    val summary: StatsSummary = StatsSummary(
        totalCost = 0.0,
        totalWeight = 0.0,
        topDish = null,
        topDishCost = 0.0,
        priceChanges = 0,
        priceUpCount = 0,
        priceDownCount = 0
    ),
    val priceDynamics: List<PriceChangeItem> = emptyList(),
    val selectedFilter: TimeFilter = TimeFilter.Week
)