package com.example.qrmealtrack.presentation.stats.model

import androidx.compose.runtime.Immutable

@Immutable
data class StatsUiModel(
    val formattedWeight: String,
    val formattedCost: String,
)