package com.example.qrmealtrack.data.mapper

import com.example.qrmealtrack.data.local.ReceiptEntity
import com.example.qrmealtrack.domain.model.Meal
import com.example.qrmealtrack.domain.model.Receipt

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
            unitPrice = meal.unitPrice,
            total = this.total
        )
    }
}

fun List<ReceiptEntity>.toDomainReceipts(): List<Receipt> {
    return this.groupBy { Triple(it.fiscalCode, it.dateTime, it.type) }.map { (_, group) ->
        Receipt(
            fiscalCode = group.first().fiscalCode,
            enterprise = group.first().enterprise,
            dateTime = group.first().dateTime,
            type = group.first().type,
            total = group.first().total, // или пересчитать
            items = group.map {
                Meal(
                    name = it.itemName,
                    weight = it.weight,
                    unitPrice = it.unitPrice,
                    price = it.price
                )
            }
        )
    }
}

// Преобразование ReceiptEntity -> Receipt с 1 блюдом
fun ReceiptEntity.toDomain(): Receipt {
    return Receipt(
        fiscalCode = fiscalCode,
        enterprise = enterprise,
        dateTime = dateTime,
        type = type,
        items = listOf(
            Meal(
                name = itemName,
                weight = weight,
                unitPrice = unitPrice,
                price = price
            )
        ),
        total = total
    )
}