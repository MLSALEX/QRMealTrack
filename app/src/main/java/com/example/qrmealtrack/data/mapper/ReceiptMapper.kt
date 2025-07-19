package com.example.qrmealtrack.data.mapper

import com.example.qrmealtrack.data.local.ReceiptEntity
import com.example.qrmealtrack.data.local.ReceiptItemEntity
import com.example.qrmealtrack.data.local.relation.ReceiptWithItems
import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.domain.model.ReceiptCategory
import com.example.qrmealtrack.domain.model.ReceiptItem
import com.example.qrmealtrack.presentation.utils.ParsedReceipt

// Receipt → ReceiptEntity
fun Receipt.toEntity(): ReceiptEntity {
    return ReceiptEntity(
        id = id,
        fiscalCode = fiscalCode,
        enterprise = enterprise,
        total = total,
        dateTime = dateTime,
        category = category.key
    )
}

// ReceiptItem → ReceiptItemEntity (нужно передать id чека)
fun ReceiptItem.toEntity(receiptId: Long): ReceiptItemEntity {
    return ReceiptItemEntity(
        receiptId = receiptId,
        name = name,
        weight = weight,
        unitPrice = unitPrice,
        price = price,
        isWeightBased = isWeightBased,
        category = category.key
    )
}
fun ReceiptWithItems.toDomain(): Receipt {
    return Receipt(
        id = receipt.id,
        fiscalCode = receipt.fiscalCode,
        enterprise = receipt.enterprise,
        dateTime = receipt.dateTime,
        type = "", // если нужно — добавь type в ReceiptEntity
        total = receipt.total,
        category = ReceiptCategory.fromKey(receipt.category),
        items = items.map {
            ReceiptItem(
                name = it.name,
                weight = it.weight,
                unitPrice = it.unitPrice,
                price = it.price,
                isWeightBased = it.isWeightBased,
                category =  ReceiptCategory.fromKey(it.category)
            )
        }
    )
}

fun parseQrToReceipt(rawValue: String): Receipt? {
    return try {
        val parts = rawValue.split(";")
        val weight = parts[3].toDouble()
        val pricePerUnit = parts[4].toDouble()
        val price = weight * pricePerUnit
        val isWeightBased = weight in 0.01..10.0

        val item = ReceiptItem(
            name = parts[2],
            weight = weight,
            unitPrice = pricePerUnit,
            price = price,
            isWeightBased = isWeightBased,
            category = ReceiptCategory.NO_CATEGORY
        )

        Receipt(
            id = 0L,
            fiscalCode = parts[0],
            enterprise = parts[1],
            dateTime = System.currentTimeMillis(),
            type = parts[5],
            total = price,
            category = ReceiptCategory.NO_CATEGORY,
            items = listOf(item)
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
fun ParsedReceipt.toDomain(): Receipt {
    return Receipt(
        id = 0L,
        fiscalCode = fiscalCode,
        enterprise = enterprise,
        dateTime = dateTime,
        type = "Web",
        total = items.sumOf { it.price },
        category = ReceiptCategory.NO_CATEGORY,
        items = items.map {
            ReceiptItem(
                name = it.name,
                weight = it.weight,
                unitPrice = it.unitPrice,
                price = it.price,
                isWeightBased = it.isWeightBased,
                category = ReceiptCategory.fromKey(it.category)
            )
        }
    )
}
