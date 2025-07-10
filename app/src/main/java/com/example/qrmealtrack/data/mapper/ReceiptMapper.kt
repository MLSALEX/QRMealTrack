package com.example.qrmealtrack.data.mapper

import com.example.qrmealtrack.data.local.ReceiptEntity
import com.example.qrmealtrack.domain.model.Meal
import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.presentation.model.MealUiModel
import com.example.qrmealtrack.presentation.model.ReceiptUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Преобразование Receipt -> List<ReceiptEntity>
fun Receipt.toEntityList(): List<ReceiptEntity> {
    return items.map { meal ->
        ReceiptEntity(
            fiscalCode = this.fiscalCode,
            enterprise = this.enterprise,
            dateTime = this.dateTime,
            type = this.type,
            itemName = meal.name,
            weight = meal.weight,
            price = meal.price,
        )
    }
}

fun List<ReceiptEntity>.toDomainReceipts(): List<Receipt> {
    return this.groupBy { Triple(it.fiscalCode, it.dateTime, it.type) }
        .map { (_, group) ->
            val first = group.first()
            val items = group.map {
                Meal(
                    name = it.itemName,
                    weight = it.weight,
                    unitPrice = if (it.weight != 0.0) it.price / it.weight else 0.0,
                    price = it.price
                )
            }

        Receipt(
            id = first.id,
            fiscalCode = first.fiscalCode,
            enterprise = first.enterprise,
            dateTime = first.dateTime,
            type = first.type,
            items = items,
            total = items.sumOf { it.price }
        )
    }
}

fun parseQrToReceipt(rawValue: String): ReceiptEntity? {
    return try {
        val parts = rawValue.split(";")
        val weight = parts[3].toDouble()
        val pricePerUnit = parts[4].toDouble()
        val price = weight * pricePerUnit
        ReceiptEntity(
            fiscalCode = parts[0],
            enterprise = parts[1],
            itemName = parts[2],
            weight = weight,
            price = price,
            dateTime = System.currentTimeMillis(),
            type = parts[5]
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Receipt.toUiModel(): ReceiptUiModel {
    val formattedDate = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        .format(Date(dateTime))

    return ReceiptUiModel(
        id = id,
        fiscalCode = fiscalCode,
        dateTime = dateTime,
        date = formattedDate,
        total = "%.2f".format(total),
        items = items.map {
            return@map MealUiModel(
                name = it.name,
                weight = "%.2f".format(it.weight),
                unitPrice = "%.2f".format(it.unitPrice),
                price = "%.2f".format(it.price)
            )
        }
    )
}