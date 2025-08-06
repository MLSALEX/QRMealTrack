package com.example.qrmealtrack.presentation.trends.model.mapper


import androidx.compose.ui.graphics.Color
import com.example.qrmealtrack.domain.model.ChartPoint
import com.example.qrmealtrack.presentation.trends.model.UiChartPoint

fun ChartPoint.toUiChartPoint(color: Color): UiChartPoint {
    return UiChartPoint(
        category = category.replaceFirstChar { it.uppercaseChar() }, // ✅ Название с заглавной буквы
        value = value,
        dateLabel = dateLabel,
        color = color
    )
}