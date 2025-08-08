package com.example.qrmealtrack.domain.usecase

import com.example.qrmealtrack.data.mapper.toDomain
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import com.example.qrmealtrack.presentation.utils.ParsedReceipt
import javax.inject.Inject

class SaveParsedReceiptUseCase @Inject constructor(
    private val repository: ReceiptRepository
) {
    suspend operator fun invoke(parsed: ParsedReceipt): Result<Unit> {
        if (parsed.items.isEmpty()) {
            return Result.failure(IllegalArgumentException("Empty list"))
        }

        val existing = repository.getReceiptsByFiscalCodeAndDate(parsed.fiscalCode, parsed.dateTime)
        if (existing.isNotEmpty()) {
            return Result.failure(IllegalStateException("This receipt has already been saved"))
        }

        repository.insertReceipt(parsed.toDomain())

        return Result.success(Unit)
    }
}