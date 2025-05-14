package com.example.qrmealtrack.domain.usecase

import com.example.qrmealtrack.domain.repository.ReceiptRepository
import com.example.qrmealtrack.presentation.utils.ParsedReceipt
import javax.inject.Inject

class SaveParsedReceiptUseCase @Inject constructor(
    private val repository: ReceiptRepository
) {
    suspend operator fun invoke(parsed: ParsedReceipt): Result<Unit> {
        if (parsed.items.isEmpty()) {
            return Result.failure(IllegalArgumentException("Пустой список"))
        }

        val fiscalCode = parsed.items.first().fiscalCode
        val dateTime = parsed.items.first().dateTime

        val existing = repository.getReceiptsByFiscalCodeAndDate(fiscalCode, dateTime)
        if (existing.isNotEmpty()) {
            return Result.failure(IllegalStateException("Этот чек уже сохранён"))
        }

        parsed.items.forEach { receipt ->
            repository.insertReceipt(receipt)
        }

        return Result.success(Unit)
    }
}