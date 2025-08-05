package com.example.qrmealtrack.presentation.trends.mock

import com.example.qrmealtrack.presentation.trends.model.ChartPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

val mockDayPoints = List(90) { index ->
    val date = LocalDate.now().minusDays((89 - index).toLong()) // последние 90 дней
    val formatter = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)
    ChartPoint(
        category = "Meal",
        value = (20..80).random().toFloat(),
        dateLabel = date.format(formatter)
    )
}

val mockWeekPoints = List(12) { index ->
    ChartPoint(
        category = "Meal",
        value = (200..500).random().toFloat(),
        dateLabel = "Week ${index + 1}"
    )
}

val mockMonthPoints = List(3) { index ->
    ChartPoint(
        category = "Meal",
        value = (1000..2000).random().toFloat(),
        dateLabel = "Month ${index + 1}"
    )
}
