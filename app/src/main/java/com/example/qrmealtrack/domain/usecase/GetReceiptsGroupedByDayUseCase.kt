package com.example.qrmealtrack.domain.usecase

import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.example.qrmealtrack.data.mapper.groupByDay

class GetReceiptsGroupedByDayUseCase @Inject constructor(
    private val repository: ReceiptRepository
) {
    operator fun invoke(): Flow<GroupedReceiptsResult> {
        return repository.getAllReceipts().map { receipts ->
            val grouped = receipts.groupByDay()
            val totals = grouped.mapValues { (_, receiptsForDay) ->
                receiptsForDay.sumOf { it.total }
            }

            GroupedReceiptsResult(
                receiptsByDay = grouped,
                totalsByDay = totals
            )
        }
    }
}

data class GroupedReceiptsResult(
    val receiptsByDay: Map<String, List<Receipt>>,
    val totalsByDay: Map<String, Double>
)