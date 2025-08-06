package com.example.qrmealtrack.domain.usecase

import com.example.qrmealtrack.presentation.trends.components.GranularityType
import com.example.qrmealtrack.presentation.trends.mock.ChartDataSource
import com.example.qrmealtrack.presentation.trends.model.UiChartPoint
import com.example.qrmealtrack.presentation.trends.model.mapper.toUiChartPoint
import com.example.qrmealtrack.presentation.utils.CategoryColorProvider
import javax.inject.Inject

class GetFilteredChartPointsUseCase @Inject constructor(
    private val chartDataSource: ChartDataSource,
    private val categoryColorProvider: CategoryColorProvider
) {
    operator fun invoke(
        granularity: GranularityType,
        selectedKeys: List<String>
    ): Map<String, List<UiChartPoint>> {
        val allPoints = chartDataSource.getPoints(granularity)

        return allPoints
            .filter { it.category in selectedKeys }
            .groupBy { it.category }
            .mapValues { (category, points) ->
                val color = categoryColorProvider.getColorForCategory(category)
                points.map { it.toUiChartPoint(color) }
            }
    }
}
