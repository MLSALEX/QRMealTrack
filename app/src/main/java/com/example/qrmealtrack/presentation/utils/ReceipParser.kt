package com.example.qrmealtrack.presentation.utils

import android.util.Log
import com.example.qrmealtrack.data.local.ReceiptEntity
import java.text.SimpleDateFormat
import java.util.Locale

data class ParsedReceipt(
    val items: List<ReceiptEntity>,
    val dateTime: Long,
    val total: Double
)

fun parseTextToReceipts(text: String): ParsedReceipt? {
    val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }

    val receipts = mutableListOf<ReceiptEntity>()
    var total = 0.0

    // Фискальный код
    val fiscalCodeLine = lines.find { it.contains("COD FISCAL", ignoreCase = true) }
    val fiscalCode = fiscalCodeLine?.substringAfter("COD FISCAL")?.replace(":", "")?.trim() ?: "UNKNOWN"

    // Дата и время
    val datePart = lines.firstNotNullOfOrNull {
        Regex("""\d{2}\.\d{2}\.\d{4}""").find(it)?.value
    }
    val timePart = lines.firstNotNullOfOrNull {
        Regex("""\d{2}:\d{2}:\d{2}""").find(it)?.value
    }
    Log.d("🕒DATE_DEBUG", "Найдена дата: $datePart | время: $timePart")
    val parsedDateTime: Long? = if (!datePart.isNullOrBlank() && !timePart.isNullOrBlank()) {
        try {
            val format = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
            format.parse("$datePart $timePart")?.time
        } catch (e: Exception) {
            Log.e("🕒ERROR", "Ошибка парсинга даты: $datePart $timePart", e)
            null
        }
    } else null
    val finalDateTime = parsedDateTime ?: System.currentTimeMillis()

    try {
        val itemRegex = Regex("""(.*)\s([0-9.,]+)\s*[x×]\s*([0-9.,]+)""")
        val weightLineRegex = Regex("""^[0-9.,]+\s*[x×]\s*[0-9.,]+$""")
        val priceLineRegex = Regex("""^[0-9.,]+\s*[A-Z]?$""")

        var i = 0
        while (i < lines.size - 1) {
            val current = lines[i]
            val next = lines.getOrNull(i + 1) ?: ""
            val inlineMatch = itemRegex.find(current)

            if (inlineMatch != null && priceLineRegex.matches(next)) {
                val itemName = inlineMatch.groupValues[1].trim()
                val weight = inlineMatch.groupValues[2].replace(",", ".").toDoubleOrNull() ?: 0.0
                val unitPrice = inlineMatch.groupValues[3].replace(",", ".").toDoubleOrNull() ?: 0.0
                val price = next.replace(Regex("[^0-9.,]"), "").replace(",", ".").toDoubleOrNull() ?: 0.0

                Log.d("🍽️PARSE", "Блюдо: $itemName | Вес: $weight г | Цена: $price")

                receipts.add(
                    ReceiptEntity(
                        fiscalCode = fiscalCode,
                        enterprise = "Web Receipt",
                        itemName = itemName,
                        weight = weight,
                        unitPrice = unitPrice,
                        price = price,
                        dateTime = finalDateTime,
                        type = "Web",
                        total = price
                    )
                )
                i += 2
                continue
            }

            val weightMatch = weightLineRegex.find(next)
            val totalLine = lines.getOrNull(i + 2) ?: ""

            if (weightMatch != null && priceLineRegex.matches(totalLine)) {
                val itemName = current
                val parts = next.split(Regex("[x×]")).map { it.trim().replace(",", ".") }
                val weight = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
                val unitPrice = parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0
                val price = totalLine.replace(Regex("[^0-9.,]"), "").replace(",", ".").toDoubleOrNull() ?: 0.0

                receipts.add(
                    ReceiptEntity(
                        fiscalCode = fiscalCode,
                        enterprise = "Web Receipt",
                        itemName = itemName,
                        weight = weight,
                        unitPrice = unitPrice,
                        price = price,
                        dateTime = finalDateTime,
                        type = "Web",
                        total = price
                    )
                )
                i += 3
                continue
            }

            i++
        }

        // Итоговая сумма
        val totalLineIndex = lines.indexOfFirst { it.contains("TOTAL", ignoreCase = true) }
        if (totalLineIndex != -1) {
            val amountLine = lines.getOrNull(totalLineIndex + 1)
            total = amountLine?.replace(",", ".")?.toDoubleOrNull() ?: receipts.sumOf { it.price }
        } else {
            total = receipts.sumOf { it.price }
        }

        return if (receipts.isNotEmpty()) ParsedReceipt(receipts, finalDateTime, total) else null
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

