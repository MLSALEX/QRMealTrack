package com.example.qrmealtrack.presentation.model

import com.example.qrmealtrack.R
import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.domain.model.ReceiptCategory
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
                    categoryKey = it.category.key
                )
            },
            isToday = dateFormatter.isToday(domain.dateTime),
            category = domain.category
        )
    }
}

// Extension to round
fun Double.roundTo2Decimals(): Double =
    (this * 100).toInt() / 100.0

fun ReceiptCategory.iconRes(): Int = when (this) {
    ReceiptCategory.NO_CATEGORY -> R.drawable.no_categ
    ReceiptCategory.MEALS -> R.drawable.plate
    ReceiptCategory.CLOTHING -> R.drawable.clothing
    ReceiptCategory.BEAUTY -> R.drawable.beauty
    ReceiptCategory.TRANSPORT -> R.drawable.transport
    ReceiptCategory.GROCERIES -> R.drawable.cart
}

fun ReceiptCategory.displayName(): String = when (this) {
    ReceiptCategory.NO_CATEGORY -> "No Category"
    ReceiptCategory.MEALS -> "Meals"
    ReceiptCategory.CLOTHING -> "Clothing"
    ReceiptCategory.BEAUTY -> "Beauty"
    ReceiptCategory.TRANSPORT -> "Transport"
    ReceiptCategory.GROCERIES -> "Groceries"
}