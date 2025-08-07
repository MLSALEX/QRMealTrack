package com.example.qrmealtrack.presentation.trends.model.mapper


import com.example.qrmealtrack.domain.model.ChartPoint
import com.example.qrmealtrack.presentation.trends.components.GranularityType
import com.example.qrmealtrack.presentation.trends.model.UiChartPoint
import com.example.qrmealtrack.presentation.utils.CategoryColorProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

class ChartPointUiMapper @Inject constructor(
    private val categoryColorProvider: CategoryColorProvider
) {
    fun map(
        chartPoints: Map<String, List<ChartPoint>>,
        granularity: GranularityType
    ): Map<String, List<UiChartPoint>> {
        return chartPoints.mapValues { (category, points) ->
            val color = categoryColorProvider.getColorForCategory(category)
            points.map {
                UiChartPoint(
                    category = category.replaceFirstChar { it.uppercaseChar() },
                    value = it.value,
                    rawDate = it.localDate, // 🆕 для сортировки по времени
                    dateLabel = it.localDate.formatLabel(granularity),
                    color = color
                )
            }
        }
    }
}

fun LocalDate.formatLabel(granularity: GranularityType): String {
    return when (granularity) {
        GranularityType.DAY -> this.format(DateTimeFormatter.ofPattern("dd MMM"))
        GranularityType.WEEK -> "Wk ${this.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())}"
        GranularityType.MONTH -> this.format(DateTimeFormatter.ofPattern("MMM yyyy"))
    }
}