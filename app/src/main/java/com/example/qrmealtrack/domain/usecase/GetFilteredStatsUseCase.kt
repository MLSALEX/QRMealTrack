package com.example.qrmealtrack.domain.usecase

import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import com.example.qrmealtrack.presentation.stats.TimeFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@Immutable
data class StatsSummary(
    val totalCost: Double,
    val totalWeight: Double,
    val topDish: String?,
    val topDishCost: Double,
)

class GetFilteredStatsUseCase @Inject constructor(
    private val repository: ReceiptRepository
) {

    operator fun invoke(filter: TimeFilter): Flow<StatsSummary> {
        return repository.getAllReceipts().map { receipts ->
            val filtered = filterByTime(receipts, filter)
            val allMeals = filtered.flatMap { it.items }

            val topDish = allMeals.groupBy { it.name }
                .maxByOrNull { it.value.size }?.key

            val topDishCost = allMeals
                .filter { it.name == topDish }
                .sumOf { it.price }


            StatsSummary(
                totalCost = calculateTotalCost(filtered),
                totalWeight = calculateTotalWeight(filtered),
                topDish = topDish,
                topDishCost = topDishCost,
            )
        }
    }

    private fun filterByTime(receipts: List<Receipt>, filter: TimeFilter): List<Receipt> {
        val now = System.currentTimeMillis()
        return receipts.filter {
            when (filter) {
                TimeFilter.Today -> it.dateTime >= now - 1.days
                TimeFilter.Week -> it.dateTime >= now - 7.days
                TimeFilter.Month -> it.dateTime >= now - 30.days
                TimeFilter.All -> true
            }
        }
    }

    private fun calculateTotalCost(receipts: List<Receipt>): Double =
        receipts.sumOf { it.total }

    private fun calculateTotalWeight(receipts: List<Receipt>): Double =
        receipts.sumOf { it.items.sumOf { meal -> meal.weight } }

    private fun findTopDish(receipts: List<Receipt>): String? =
        receipts.flatMap { it.items }
            .groupBy { it.name }
            .maxByOrNull { it.value.size }?.key

    private fun countPriceChanges(receipts: List<Receipt>): Int =
        receipts.flatMap { it.items }
            .groupBy { it.name }
            .count { group -> group.value.map { it.unitPrice }.distinct().size > 1 }
}

// Вспомогательное расширение
val Int.days: Long get() = this * 24 * 60 * 60 * 1000L