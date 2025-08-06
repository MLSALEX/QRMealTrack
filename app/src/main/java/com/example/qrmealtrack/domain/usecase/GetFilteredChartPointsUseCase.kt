package com.example.qrmealtrack.domain.usecase

import android.util.Log
import com.example.qrmealtrack.presentation.trends.components.GranularityType
import com.example.qrmealtrack.presentation.trends.mock.ChartDataSource
import com.example.qrmealtrack.presentation.trends.model.UiChartPoint
import com.example.qrmealtrack.presentation.utils.CategoryColorProvider
import javax.inject.Inject

class GetFilteredChartPointsUseCase @Inject constructor(
    private val chartDataSource: ChartDataSource,
    private val categoryColorProvider: CategoryColorProvider
) {
    operator fun invoke(
        granularity: GranularityType,
        selectedKeys: List<String>
    ): List<UiChartPoint> {
        Log.d("GetFilteredChartPoints", "selectedKeys: $selectedKeys")
        val points = chartDataSource.getPoints(granularity)
            .filter { it.category in selectedKeys }
            .map {
                UiChartPoint(
                    category = it.category,
                    value = it.value,
                    dateLabel = it.dateLabel,
                    color = categoryColorProvider.getColorForCategory(it.category)
                )
            }

        Log.d("GetFilteredChartPoints", "points: ${points.map { it.category }}")
        return points
    }
}