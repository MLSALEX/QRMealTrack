package com.example.qrmealtrack.domain.usecase

import com.example.qrmealtrack.domain.model.PriceChangeItem
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPriceDynamicsUseCase(private val repository: ReceiptRepository) {
    operator fun invoke(): Flow<List<PriceChangeItem>> {
        return repository.getAllReceipts()
            .map { receipts ->
                receipts
                    .sortedBy { it.dateTime }
                    .groupBy { it.itemName }
                    .mapNotNull { (dish, items) ->
                        if (items.size < 2) return@mapNotNull null

                        val oldPrice = items.first().price
                        val newPrice = items.last().price

                        if (oldPrice != newPrice) {
                            PriceChangeItem(
                                dishName = dish,
                                isIncreased = newPrice > oldPrice,
                                difference = kotlin.math.abs(newPrice - oldPrice)
                            )
                        } else null
                    }
                    .sortedByDescending { it.difference }
            }
    }
}