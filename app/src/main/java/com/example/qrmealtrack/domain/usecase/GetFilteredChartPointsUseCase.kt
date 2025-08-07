package com.example.qrmealtrack.domain.usecase

import com.example.qrmealtrack.domain.model.ChartPoint
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import com.example.qrmealtrack.presentation.trends.components.GranularityType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class GetFilteredChartPointsUseCase @Inject constructor(
    private val repository: ReceiptRepository
) {
    operator fun invoke(
        granularity: GranularityType,
        selectedKeys: List<String>
    ): Flow<Map<String, List<ChartPoint>>> {
        return repository.getAllReceipts()
            .map { receipts ->
                receipts
                    .filter { it.category.key in selectedKeys }
                    .map {
                        val localDate = it.dateTime.toLocalDateByGranularity(granularity)

                        ChartPoint(
                            category = it.category.key,
                            value = it.total.toFloat(),
                            localDate = localDate,
                            originalDate = it.dateTime
                        )
                    }
                    .groupBy { it.category }
                    .mapValues { (_, points) ->
                        points.sortedBy { it.originalDate }
                    }
            }
    }
}

fun Long.toLocalDateByGranularity(granularity: GranularityType): LocalDate {
    val date = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
    return when (granularity) {
        GranularityType.DAY -> date
        GranularityType.WEEK -> date.with(DayOfWeek.MONDAY)
        GranularityType.MONTH -> date.withDayOfMonth(1)
    }
}

