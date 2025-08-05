package com.example.qrmealtrack.presentation.trends.model

import androidx.compose.runtime.Immutable

@Immutable
data class ChartPoint(
    val category: String,
    val value: Float,
    val dateLabel: String
)