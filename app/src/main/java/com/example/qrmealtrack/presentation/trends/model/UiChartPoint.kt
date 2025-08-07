package com.example.qrmealtrack.presentation.trends.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import java.time.LocalDate

@Immutable
data class UiChartPoint(
    val category: String,
    val value: Float,
    val rawDate: LocalDate,
    val dateLabel: String,
    val color: Color
)