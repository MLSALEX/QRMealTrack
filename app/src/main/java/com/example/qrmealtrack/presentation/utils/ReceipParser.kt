package com.example.qrmealtrack.presentation.utils

import android.util.Log
import com.example.qrmealtrack.data.local.ReceiptEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

data class ParsedReceipt(
    val items: List<ReceiptEntity>,
    val dateTime: Long,
    val total: Double
)
object ReceiptRegex {
    val item = Regex("""(.*)\s([0-9.,]+)\s*[x×]\s*([0-9.,]+)""")
    val weightLine = Regex("""^[0-9.,]+\s*[x×]\s*[0-9.,]+$""")
    val priceLine = Regex("""^[0-9.,]+\s*[A-Z]?$""")
    val date = Regex("""\d{2}\.\d{2}\.\d{4}""")
    val time = Regex("""\d{2}:\d{2}:\d{2}""")
    val multiplySign = Regex("[x×]") // для split()
    val priceCleanup = Regex("[^0-9.,]")
}
suspend fun parseTextToReceipts(text: String): ParsedReceipt? = withContext(Dispatchers.Default) {
    val lines = text.lines()
        .asSequence()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .toList()

    if (lines.isEmpty()) return@withContext null

    val receipts = mutableListOf<ReceiptEntity>()
    var total = 0.0

    // COD FISCAL
    val fiscalCodeLine = lines.find { it.contains("COD FISCAL", ignoreCase = true) }
    val fiscalCode = fiscalCodeLine
        ?.substringAfter("COD FISCAL")
        ?.replace(":", "")
        ?.trim()
        ?: "UNKNOWN"

    // Дата + время
    val datePart = lines.firstNotNullOfOrNull {
        ReceiptRegex.date.find(it)?.value
    }
    val timePart = lines.firstNotNullOfOrNull {
        ReceiptRegex.time.find(it)?.value
    }

    val parsedDateTime: Long? = if (!datePart.isNullOrBlank() && !timePart.isNullOrBlank()) {
        try {
            val format = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
            format.parse("$datePart $timePart")?.time
        } catch (e: Exception) {
            null
        }
    } else null

    val finalDateTime = parsedDateTime ?: System.currentTimeMillis()

    var i = 0
    while (i < lines.size - 1) {
        val current = lines[i]
        val next = lines.getOrNull(i + 1) ?: ""
        val inlineMatch = ReceiptRegex.item.find(current)

        if (inlineMatch != null && ReceiptRegex.priceLine.matches(next)) {
            val itemName = inlineMatch.groupValues[1].trim()
            val weight = inlineMatch.groupValues[2].replace(",", ".").toDoubleOrNull() ?: 0.0
            val unitPrice = inlineMatch.groupValues[3].replace(",", ".").toDoubleOrNull() ?: 0.0
            val price = next.replace(ReceiptRegex.priceCleanup, "").replace(",", ".").toDoubleOrNull() ?: 0.0

            receipts += ReceiptEntity(
                fiscalCode = fiscalCode,
                enterprise = "Web Receipt",
                itemName = itemName,
                weight = weight,
                price = price,
                dateTime = finalDateTime,
                type = "Web"
            )

            i += 2
            continue
        }

        // Второй случай: item, nextLine: weight x unitPrice, totalLine: цена
        val weightMatch = ReceiptRegex.weightLine.matches(next)
        val totalLine = lines.getOrNull(i + 2) ?: ""

        if (weightMatch && ReceiptRegex.priceLine.matches(totalLine)) {
            val itemName = current
            val parts = next.split(ReceiptRegex.multiplySign)
                .map { it.trim().replace(",", ".") }

            val weight = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
            val unitPrice = parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0
            val price = totalLine.replace(Regex("[^0-9.,]"), "").replace(",", ".").toDoubleOrNull() ?: 0.0

            receipts += ReceiptEntity(
                fiscalCode = fiscalCode,
                enterprise = "Web Receipt",
                itemName = itemName,
                weight = weight,
                price = price,
                dateTime = finalDateTime,
                type = "Web"
            )

            i += 3
            continue
        }

        i++
    }

    // TOTAL сумма
    val totalLineIndex = lines.indexOfFirst { it.contains("TOTAL", ignoreCase = true) }
    total = if (totalLineIndex != -1) {
        lines.getOrNull(totalLineIndex + 1)?.replace(",", ".")?.toDoubleOrNull()
            ?: receipts.sumOf { it.price }
    } else {
        receipts.sumOf { it.price }
    }

    return@withContext if (receipts.isNotEmpty()) {
        ParsedReceipt(
            items = receipts,
            dateTime = finalDateTime,
            total = total
        )
    } else null
}


