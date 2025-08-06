package com.example.qrmealtrack.presentation.trends.mock

import com.example.qrmealtrack.domain.model.ChartPoint
import com.example.qrmealtrack.presentation.trends.components.GranularityType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.IsoFields
import java.util.Locale
import javax.inject.Inject

class ChartDataSource @Inject constructor() {

    // Основной публичный метод
    fun getPoints(granularity: GranularityType): List<ChartPoint> {
        val rawPoints = generateRawPoints(days = 90)

        return when (granularity) {
            GranularityType.DAY -> rawPoints
            GranularityType.WEEK -> aggregatePoints(rawPoints) { date ->
                val week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
                val year = date.year
                "Week $week, $year"
            }
            GranularityType.MONTH -> aggregatePoints(rawPoints) { date ->
                date.format(DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH))
            }
        }
    }

    // 1. Генерация сырых точек
    private fun generateRawPoints(days: Int): List<ChartPoint> {
        val formatter = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)
        val categories = listOf("meals", "clothing", "beauty", "transport", "groceries")
        val now = LocalDate.now()

        val points = mutableListOf<ChartPoint>()

        categories.forEachIndexed { categoryIndex, category ->
            repeat(days) { i ->
                val offset = categoryIndex * 2L // Для смещения, чтобы точки не совпадали
                val date = now.minusDays((days - i).toLong() - offset)
                points.add(
                    ChartPoint(
                        category = category,
                        value = (100..1000 step 50).toList().random().toFloat(),
                        dateLabel = date.format(formatter),
                        localDate = date
                    )
                )
            }
        }

        return points
    }

    // 2. Агрегация точек по неделе или месяцу
    private fun aggregatePoints(
        rawPoints: List<ChartPoint>,
        groupKeySelector: (LocalDate) -> String
    ): List<ChartPoint> {
        return rawPoints
            .groupBy { it.category to groupKeySelector(it.localDate) }
            .map { (key, group) ->
                val (category, label) = key
                val avgValue = group.map { it.value }.average().toFloat()
                ChartPoint(
                    category = category,
                    value = avgValue,
                    dateLabel = label,
                    localDate = group.first().localDate // необязательно, можно оставить
                )
            }
    }
}