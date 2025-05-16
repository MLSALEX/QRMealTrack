package com.example.qrmealtrack.data.mapper

import android.util.Log
import com.example.qrmealtrack.domain.model.Meal
import com.example.qrmealtrack.data.local.ReceiptEntity
import com.example.qrmealtrack.domain.model.Receipt
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun List<ReceiptEntity>.groupByFiscalCode(): List<Receipt> {
    return this
        .groupBy { it.fiscalCode }
        .map { (fiscalCode, entries) ->
            val first = entries.first()
            Receipt(
                fiscalCode = fiscalCode,
                enterprise = first.enterprise,
                dateTime = first.dateTime,
                type = first.type,
                items = entries.map {
                    Meal(
                        name = it.itemName,
                        weight = it.weight,
                        unitPrice = if (it.weight != 0.0) it.price / it.weight else 0.0,
                        price = it.price
                    )
                },
                total = entries.sumOf { it.price }
            )
        }
}

fun List<ReceiptEntity>.groupByDay(): Map<String, List<Receipt>> {
    val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    Log.d("🧾groupByDay", "Входящих записей: ${this.size}")
    this.forEach {
        Log.d("🧾groupByDay", "item=${it.itemName}, dateTime=${it.dateTime}")
    }
    return this
        .groupBy { dateFormatter.format(Date(it.dateTime)) }
        .mapValues { (_, entries) ->
            entries
                .groupBy { it.dateTime } // ← каждый чек по времени!
                .map { (dateTime, itemsInCheck) ->
                    val first = itemsInCheck.first()
                    Receipt(
                        fiscalCode = first.fiscalCode,
                        enterprise = first.enterprise,
                        dateTime = dateTime,
                        type = first.type,
                        items = itemsInCheck.map {
                            Meal(
                                name = it.itemName,
                                weight = it.weight,
                                unitPrice = if (it.weight != 0.0) it.price / it.weight else 0.0,
                                price = it.price
                            )
                        },
                        total = itemsInCheck.sumOf { it.price }
                    )
                }
        }
}