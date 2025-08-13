package com.example.qrmealtrack.presentation.stats

import com.example.qrmealtrack.domain.model.StatsSummary
import com.example.qrmealtrack.presentation.stats.model.CategoryUiModel
import com.example.qrmealtrack.presentation.stats.model.StatsUiModel
import com.example.qrmealtrack.presentation.stats.model.toUiModel

data class StatsUiState(
    val summary: StatsSummary = StatsSummary(
        totalCost = 0.0,
        totalWeight = 0.0
    ),
    val selectedFilter: TimeFilter = TimeFilter.Week,
    val categoryUiModels: List<CategoryUiModel> = emptyList(),
    val totalCategoryValue: Int = 0
) {
    val uiModel: StatsUiModel
        get() = summary.toUiModel()
}