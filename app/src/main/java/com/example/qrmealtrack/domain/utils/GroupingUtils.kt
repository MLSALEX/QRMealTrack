package com.example.qrmealtrack.domain.utils

import com.example.qrmealtrack.domain.model.Receipt
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun List<Receipt>.groupByDay(): Map<String, List<Receipt>> {
    val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return this.groupBy { receipt ->
        dateFormatter.format(Date(receipt.dateTime))
    }.mapValues { (_, receiptsInDay) ->
        receiptsInDay
            .groupBy { it.dateTime }
            .map { (_, group) ->
                val allMeals = group.flatMap { it.items }
                val representative = group.first()
                representative.copy(
                    items = allMeals,
                    total = allMeals.sumOf { it.price }
                )
            }
    }
}