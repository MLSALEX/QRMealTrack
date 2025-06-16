package com.example.qrmealtrack.domain.usecase

import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import com.example.qrmealtrack.presentation.stats.TimeFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetStatisticsUseCase @Inject constructor(
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

            val priceChangeGroups = allMeals.groupBy { it.name }
            val priceChanges = priceChangeGroups.count { it.value.map { it.unitPrice }.distinct().size > 1 }
            val priceUpCount = priceChangeGroups.count {
                val sorted = it.value.sortedBy { it.price }
                sorted.size >= 2 && sorted.last().price > sorted.first().price
            }
            val priceDownCount = priceChangeGroups.count {
                val sorted = it.value.sortedBy { it.price }
                sorted.size >= 2 && sorted.last().price < sorted.first().price
            }

            StatsSummary(
                totalCost = filtered.sumOf { it.total },
                totalWeight = allMeals.sumOf { it.weight },
                topDish = topDish,
                topDishCost = topDishCost,
                priceChanges = priceChanges,
                priceUpCount = priceUpCount,
                priceDownCount = priceDownCount
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
}