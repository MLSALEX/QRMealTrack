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