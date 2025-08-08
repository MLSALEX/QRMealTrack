package com.example.qrmealtrack.domain.usecase

import com.example.qrmealtrack.domain.model.PriceChangeItem
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPriceDynamicsUseCase(
    private val repository: ReceiptRepository
) {
    operator fun invoke(): Flow<List<PriceChangeItem>> {
        return repository.getAllReceipts()
            .map { receipts ->
                receipts
                    .flatMap { receipt ->
                        receipt.items.map { meal ->
                            Triple(meal.name, receipt.dateTime, meal.price)
                        }
                    }
                    .groupBy { it.first } // по блюду
                    .mapNotNull { (dish, priceList) ->
                        val sorted = priceList.sortedBy { it.second }
                        if (sorted.size < 2) return@mapNotNull null

                        val oldPrice = sorted.first().third
                        val newPrice = sorted.last().third

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