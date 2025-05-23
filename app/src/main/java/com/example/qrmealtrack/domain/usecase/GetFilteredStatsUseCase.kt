package com.example.qrmealtrack.domain.usecase

import com.example.qrmealtrack.domain.repository.ReceiptRepository
import com.example.qrmealtrack.presentation.stats.TimeFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class StatsSummary(
    val totalWeight: Double,
    val totalCost: Double,
    val topDish: String?,
    val topDishCost: Double,
    val priceChanges: Int,
    val priceUpCount: Int = 0,
    val priceDownCount: Int = 0
)

class GetFilteredStatsUseCase @Inject constructor(
    private val repository: ReceiptRepository
) {
    operator fun invoke(filter: TimeFilter): Flow<StatsSummary> {
        return repository.getAllReceipts().map { list ->
            val now = System.currentTimeMillis()
            val filtered = list.filter {
                when (filter) {
                    TimeFilter.Today -> it.dateTime >= now - 1.days
                    TimeFilter.Week -> it.dateTime >= now - 7.days
                    TimeFilter.Month -> it.dateTime >= now - 30.days
                    TimeFilter.All -> true
                }
            }

            val totalWeight = filtered.sumOf { it.weight }
            val totalCost = filtered.sumOf { it.total }
            val topDish = filtered
                .groupBy { it.itemName }
                .maxByOrNull { it.value.size }?.key

            val topDishCost = filtered
                .filter { it.itemName == topDish }
                .map { it.price }
                .average()
                .takeIf { !it.isNaN() } ?: 0.0
            val grouped = filtered.groupBy { it.itemName }

            val priceChanges = grouped.count { group ->
                group.value.map { it.price }.distinct().size > 1
            }

            val priceUpCount = grouped.count { (_, items) ->
                val sorted = items.sortedBy { it.dateTime }
                sorted.size >= 2 && sorted.first().price < sorted.last().price
            }

            val priceDownCount = grouped.count { (_, items) ->
                val sorted = items.sortedBy { it.dateTime }
                sorted.size >= 2 && sorted.first().price > sorted.last().price
            }

            StatsSummary(
                totalWeight = totalWeight,
                totalCost = totalCost,
                topDish = topDish,
                topDishCost = topDishCost,
                priceChanges = priceChanges,
                priceUpCount = priceUpCount,
                priceDownCount = priceDownCount
            )
        }
    }
}

// Вспомогательное расширение
val Int.days: Long get() = this * 24 * 60 * 60 * 1000L