package com.example.qrmealtrack.presentation.utils

import android.util.Log
import com.example.qrmealtrack.data.local.ReceiptEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

data class ParsedReceipt(
    val fiscalCode: String,
    val enterprise: String,
    val dateTime: Long,
    val items: List<ParsedItem>
)

data class ParsedItem(
    val name: String,
    val weight: Double,
    val unitPrice: Double,
    val price: Double,
    val isWeightBased: Boolean,
    val category: String? = null
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
        .map { it.trim() }
        .filter { it.isNotBlank() }

    if (lines.isEmpty()) return@withContext null
    val parsedItems = mutableListOf<ParsedItem>()

    // COD FISCAL
    val fiscalCodeLine = lines.find { it.contains("COD FISCAL", ignoreCase = true) }
    val fiscalCode = fiscalCodeLine
        ?.substringAfter("COD FISCAL")
        ?.replace(":", "")
        ?.trim()
        ?: "UNKNOWN"

    // Предполагаемый продавец
    val fiscalIndex = lines.indexOf(fiscalCodeLine)
    val enterprise = if (fiscalIndex > 0) lines[fiscalIndex - 1] else "Web Receipt"

    // Дата + время
    val datePart = lines.firstNotNullOfOrNull { ReceiptRegex.date.find(it)?.value }
    val timePart = lines.firstNotNullOfOrNull { ReceiptRegex.time.find(it)?.value }

    val finalDateTime = try {
        if (!datePart.isNullOrBlank() && !timePart.isNullOrBlank()) {
            val format = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
            format.parse("$datePart $timePart")?.time ?: System.currentTimeMillis()
        } else System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }

    var i = 0
    while (i < lines.size - 1) {
        val current = lines[i]
        val next = lines.getOrNull(i + 1) ?: ""
        val afterNext = lines.getOrNull(i + 2) ?: ""

        // Вариант 1: inline запись "название qty x unit" + "итоговая сумма"
        val inlineMatch = ReceiptRegex.item.find(current)
        if (inlineMatch != null && ReceiptRegex.priceLine.matches(next)) {
            val rawName = inlineMatch.groupValues[1].trim()
            val weight = inlineMatch.groupValues[2].replace(",", ".").toDoubleOrNull() ?: 0.0
            val unitPrice = inlineMatch.groupValues[3].replace(",", ".").toDoubleOrNull() ?: 0.0
            val price = next.replace(ReceiptRegex.priceCleanup, "").replace(",", ".").toDoubleOrNull() ?: 0.0

            val autoCategory = if (rawName.contains("CAFENEA", true)) "food" else null
            val itemName = rawName
                .replaceFirst("CAFENEA", "", ignoreCase = true)
                .replaceFirst(Regex("^[0-9]{10,}\\s+"), "")       // штрихкоды
                .replaceFirst(Regex("^[A-Z0-9]{6,}\\."), "")      // коды типа "I401001..."
                .trim()

            val isWeightBased = itemName.contains("kg", ignoreCase = true) || weight in 0.01..10.0

            parsedItems += ParsedItem(
                name = itemName,
                weight = weight,
                unitPrice = unitPrice,
                price = price,
                isWeightBased = isWeightBased,
                category = autoCategory
            )


            i += 2
            continue
        }

        // Вариант 2: название + (вес x цена) + итоговая сумма
        if (ReceiptRegex.weightLine.matches(next) && ReceiptRegex.priceLine.matches(afterNext)) {
            val rawName = current
            val parts = next.split(ReceiptRegex.multiplySign).map { it.trim().replace(",", ".") }
            val weight = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
            val unitPrice = parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0
            val price = afterNext.replace(ReceiptRegex.priceCleanup, "").replace(",", ".").toDoubleOrNull() ?: 0.0

            val autoCategory = if (rawName.contains("CAFENEA", true)) "food" else null
            val itemName = rawName
                .replaceFirst("CAFENEA", "", ignoreCase = true)
                .replaceFirst(Regex("^[0-9]{10,}\\s+"), "")       // штрихкоды
                .replaceFirst(Regex("^[A-Z0-9]{6,}\\."), "")      // коды типа "I401001..."
                .trim()

            val isWeightBased = itemName.contains("kg", ignoreCase = true) || weight in 0.01..10.0

            parsedItems += ParsedItem(
                name = itemName,
                weight = weight,
                unitPrice = unitPrice,
                price = price,
                isWeightBased = isWeightBased,
                category = autoCategory
            )

            i += 3
            continue
        }

        i++
    }

    // TOTAL
    val total = parsedItems.sumOf { it.price }

    return@withContext if (parsedItems.isNotEmpty()) {
        ParsedReceipt(
            fiscalCode = fiscalCode,
            enterprise = enterprise,
            dateTime = finalDateTime,
            items = parsedItems
        )
    } else null
}


