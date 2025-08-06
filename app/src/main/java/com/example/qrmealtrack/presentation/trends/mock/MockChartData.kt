package com.example.qrmealtrack.presentation.trends.mock

import com.example.qrmealtrack.domain.model.ChartPoint
import com.example.qrmealtrack.presentation.trends.components.GranularityType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class ChartDataSource @Inject constructor() {

    fun getPoints(granularity: GranularityType): List<ChartPoint> {
        return when (granularity) {
            GranularityType.DAY -> generateMockPoints(days = 30)
            GranularityType.WEEK -> generateMockPoints(days = 12 * 7)  // 12 недель
            GranularityType.MONTH -> generateMockPoints(days = 3 * 30) // 3 месяца
        }
    }

    private fun generateMockPoints(days: Int): List<ChartPoint> {
        val formatter = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)
        val categories = listOf("meals", "clothing", "beauty", "transport", "groceries")
        val now = LocalDate.now()

        val points = mutableListOf<ChartPoint>()

        categories.forEachIndexed { categoryIndex, category ->
            repeat(days) { i ->
                val offset = categoryIndex * 2L // Смещение по дням, чтобы не было полного наложения
                val date = now.minusDays((days - i).toLong() - offset)
                points.add(
                    ChartPoint(
                        category = category,
                        value = (100..1000 step 50).toList().random().toFloat(),
                        dateLabel = date.format(formatter)
                    )
                )
            }
        }

        return points
    }
}