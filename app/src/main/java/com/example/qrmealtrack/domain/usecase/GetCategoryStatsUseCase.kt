package com.example.qrmealtrack.domain.usecase

import com.example.qrmealtrack.domain.repository.ReceiptRepository
import com.example.qrmealtrack.domain.time.DateRangeProvider
import com.example.qrmealtrack.presentation.stats.TimeFilter
import com.example.qrmealtrack.presentation.stats.components.CategoryStat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCategoryStatsUseCase @Inject constructor(
    private val repository: ReceiptRepository,
    private val dateRangeProvider: DateRangeProvider
) {
    operator fun invoke(filter: TimeFilter): Flow<List<CategoryStat>> {
        val (start, _) = dateRangeProvider.rangeFor(filter)
        return repository.getAllReceipts().map { receipts ->
            receipts
                .asSequence()
                .filter { it.dateTime >= start }
                .groupBy { it.category.key }
                .map { (key, group) -> CategoryStat(category = key, total = group.sumOf { it.total }) }
                .sortedByDescending { it.total }
        }
    }
}

