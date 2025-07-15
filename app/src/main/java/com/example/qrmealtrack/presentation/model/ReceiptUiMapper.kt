package com.example.qrmealtrack.presentation.model

import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.presentation.utils.DateFormatter
import javax.inject.Inject

class ReceiptUiMapper @Inject constructor(
    private val dateFormatter: DateFormatter
) {

    fun map(domain: Receipt): ReceiptUiModel {
        return ReceiptUiModel(
            id = domain.id,
            fiscalCode = domain.fiscalCode,
            enterprise = domain.enterprise,
            dateTime = domain.dateTime,
            date = dateFormatter.formatDateTime(domain.dateTime),
            total = domain.total.roundTo2Decimals(),
            items = domain.items.map {
                MealUiModel(
                    name = it.name,
                    weight = "%.2f".format(it.weight),
                    unitPrice = "%.2f".format(it.unitPrice),
                    price = "%.2f".format(it.price),
                    isWeightBased = it.isWeightBased,
                    category = it.category
                )
            },
            isToday = dateFormatter.isToday(domain.dateTime)
        )
    }
}

// Extension to round
fun Double.roundTo2Decimals(): Double =
    (this * 100).toInt() / 100.0