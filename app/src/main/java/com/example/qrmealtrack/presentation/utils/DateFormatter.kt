package com.example.qrmealtrack.presentation.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class DateFormatter @Inject constructor() {

    private val dateTimeFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    fun formatDateTime(timestamp: Long): String =
        dateTimeFormat.format(Date(timestamp))

    fun formatDay(timestamp: Long): String =
        dayFormat.format(Date(timestamp))

    fun parseDay(dayStr: String): Date? =
        try { dayFormat.parse(dayStr) } catch (_: Exception) { null }

    fun isToday(timestamp: Long): Boolean {
        val todayStr = dayFormat.format(Date())         // Сегодняшняя дата
        val receiptDayStr = dayFormat.format(Date(timestamp)) // День чека
        return todayStr == receiptDayStr
    }
}
