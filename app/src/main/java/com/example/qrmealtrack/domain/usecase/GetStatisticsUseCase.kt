package com.example.qrmealtrack.domain.usecase

import com.example.qrmealtrack.domain.model.ReceiptCategory
import com.example.qrmealtrack.domain.model.StatsSummary
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import com.example.qrmealtrack.domain.time.DateRangeProvider
import com.example.qrmealtrack.presentation.stats.TimeFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetStatisticsUseCase @Inject constructor(
    private val repository: ReceiptRepository,
    private val dateRangeProvider: DateRangeProvider
) {
    operator fun invoke(filter: TimeFilter): Flow<StatsSummary> {
        val (start, _) = dateRangeProvider.rangeFor(filter)
        return repository.getAllReceipts().map { receipts ->
            val mealsReceipts = receipts.asSequence()
                .filter { it.dateTime >= start && it.category == ReceiptCategory.MEALS }
                .toList()
            val allMeals = mealsReceipts.flatMap { it.items }
            StatsSummary(
                totalCost = mealsReceipts.sumOf { it.total },
                totalWeight = allMeals.sumOf { it.weight }
            )
        }
    }
}