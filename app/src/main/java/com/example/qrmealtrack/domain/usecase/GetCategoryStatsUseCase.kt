package com.example.qrmealtrack.domain.usecase

import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import com.example.qrmealtrack.presentation.stats.TimeFilter
import com.example.qrmealtrack.presentation.stats.components.CategoryStat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCategoryStatsUseCase @Inject constructor(
    private val repository: ReceiptRepository
) {
    operator fun invoke(filter: TimeFilter): Flow<List<CategoryStat>> {
        return repository.getAllReceipts().map { receipts ->
            val filtered = filterByTime(receipts, filter)
            filtered
                .groupBy { it.category.key }
                .map { (categoryKey, receiptsInCategory) ->
                    CategoryStat(
                        category = categoryKey,
                        total = receiptsInCategory.sumOf { it.total }
                    )
                }
                .sortedByDescending { it.total }
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

