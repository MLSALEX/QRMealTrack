package com.example.qrmealtrack.data.mapper

import com.example.qrmealtrack.data.local.ReceiptEntity
import com.example.qrmealtrack.domain.model.Meal
import com.example.qrmealtrack.domain.model.Receipt

// Преобразование Receipt -> List<ReceiptEntity>
fun Receipt.toEntityList(): List<ReceiptEntity> {
    return items.map { meal ->
        val isWeight = meal.weight < 10.0
        val autoCategory = if (enterprise.contains("CAFENEA", true)) "food" else null

        ReceiptEntity(
            fiscalCode = this.fiscalCode,
            enterprise = this.enterprise,
            dateTime = this.dateTime,
            type = this.type,
            itemName = meal.name,
            weight = meal.weight,
            price = meal.price,
            isWeightBased = isWeight,
            category = autoCategory
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
                    price = it.price,
                    isWeightBased = it.isWeightBased,
                    category = it.category
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

        val isWeightBased = weight in 0.01..10.0

        ReceiptEntity(
            fiscalCode = parts[0],
            enterprise = parts[1],
            itemName = parts[2],
            weight = weight,
            price = price,
            dateTime = System.currentTimeMillis(),
            type = parts[5],
            isWeightBased = isWeightBased,
            category = null
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
