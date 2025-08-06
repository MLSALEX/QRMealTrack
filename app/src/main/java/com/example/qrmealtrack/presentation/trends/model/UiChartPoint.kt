package com.example.qrmealtrack.presentation.trends.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class UiChartPoint(
    val category: String,
    val value: Float,
    val dateLabel: String,
    val color: Color
)