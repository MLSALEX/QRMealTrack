package com.example.qrmealtrack.presentation.stats.model

import androidx.compose.runtime.Immutable
import com.example.qrmealtrack.domain.model.PriceChangeItem

@Immutable
data class StatsUiModel(
    val formattedWeight: String,
    val formattedCost: String,
    val topDish: String,
    val formattedTopDishCost: String,
    val priceDynamics: List<PriceChangeItem>
)