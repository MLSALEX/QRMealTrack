package com.example.qrmealtrack.domain.model

import java.time.LocalDate

data class ChartPoint(
    val category: String,
    val value: Float,
    val localDate: LocalDate,
    val originalDate: Long
)