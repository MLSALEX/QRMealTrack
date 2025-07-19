package com.example.qrmealtrack.presentation.model

import com.example.qrmealtrack.R
import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.presentation.components.CategoryUi
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
                ItemUiModel(
                    name = it.name,
                    weight = "%.2f".format(it.weight),
                    unitPrice = "%.2f".format(it.unitPrice),
                    price = "%.2f".format(it.price),
                    isWeightBased = it.isWeightBased,
                    category = null
                )
            },
            isToday = dateFormatter.isToday(domain.dateTime),
            category = mapReceiptCategoryToUi(domain.category)
        )
    }
}

// Extension to round
fun Double.roundTo2Decimals(): Double =
    (this * 100).toInt() / 100.0

fun mapReceiptCategoryToUi(category: String?): CategoryUi? {
    return when (category) {
        "Groceries" -> CategoryUi(R.drawable.cart, "Groceries")
        "Transport" -> CategoryUi(R.drawable.transport, "Transport")
        "Beauty" -> CategoryUi(R.drawable.beauty, "Beauty")
        "Clothing" -> CategoryUi(R.drawable.clothing, "Clothing")
        else -> null
    }
}