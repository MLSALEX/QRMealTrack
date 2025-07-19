package com.example.qrmealtrack.data.mapper

import com.example.qrmealtrack.domain.model.ReceiptItem
import com.example.qrmealtrack.data.local.ReceiptEntity
import com.example.qrmealtrack.domain.model.Receipt

//fun List<ReceiptEntity>.groupByFiscalCode(): List<Receipt> {
//    return this
//        .groupBy { it.fiscalCode }
//        .map { (fiscalCode, entries) ->
//            val first = entries.first()
//            Receipt(
//                id = first.id,
//                fiscalCode = fiscalCode,
//                enterprise = first.enterprise,
//                dateTime = first.dateTime,
//                type = first.type,
//                items = entries.map {
//                    ReceiptItem(
//                        name = it.itemName,
//                        weight = it.weight,
//                        unitPrice = if (it.weight != 0.0) it.price / it.weight else 0.0,
//                        price = it.price
//                    )
//                },
//                total = entries.sumOf { it.price }
//            )
//        }
//}
