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
    operator fun invoke(): Flow<Map<String, List<Receipt>>> {
        return repository.getAllReceipts().map { it.groupByDay() }
    }
}