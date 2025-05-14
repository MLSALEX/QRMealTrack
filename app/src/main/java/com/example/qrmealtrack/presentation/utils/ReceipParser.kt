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

    // ✅ Фискальный код
    val fiscalCodeLine = lines.find { it.contains("COD FISCAL", ignoreCase = true) }
    val fiscalCode = fiscalCodeLine?.substringAfter("COD FISCAL")?.replace(":", "")?.trim() ?: "UNKNOWN"

    // ✅ Дата + время
    val dateLine = lines.find { it.contains("DATA") }
    val timeLine = lines.find { it.contains("ORA") }
    val datePart = Regex("""\d{2}\.\d{2}\.\d{4}""").find(dateLine ?: "")?.value
    val timePart = Regex("""\d{2}:\d{2}:\d{2}""").find(timeLine ?: "")?.value
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
        for (i in 0 until lines.size - 2) {
            val nameLine = lines[i]
            val weightAndPriceLine = lines[i + 1]
            val totalLine = lines[i + 2]

            // Ищем строку формата "0.206 x 100.00"
            if (weightAndPriceLine.matches(Regex("^[0-9.,]+\\s*[x×]\\s*[0-9.,]+$")) &&
                totalLine.matches(Regex("^[0-9.,]+\\s*[Cc]$"))
            ) {
                val parts = weightAndPriceLine.split(Regex("[x×]")).map { it.trim().replace(",", ".") }
                val weight = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
                val unitPrice = parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0
                val price = totalLine.replace("C", "", ignoreCase = true).trim().replace(",", ".").toDoubleOrNull() ?: 0.0

                if (nameLine.isNotBlank() && price > 0) {
                    receipts.add(
                        ReceiptEntity(
                            fiscalCode = fiscalCode,
                            enterprise = "Web Receipt",
                            itemName = nameLine,
                            weight = weight,
                            price = price,
                            dateTime = finalDateTime,
                            type = "Web"
                        )
                    )
                }
            }
        }

        // TOTAL (если есть)
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
