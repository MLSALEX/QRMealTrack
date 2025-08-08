package com.example.qrmealtrack.domain.usecase

import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow


class GetAllReceiptsUseCase(
    private val repository: ReceiptRepository
) {
    operator fun invoke(): Flow<List<Receipt>> {
        return repository.getAllReceipts()
    }
}