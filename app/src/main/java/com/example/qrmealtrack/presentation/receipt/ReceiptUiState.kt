package com.example.qrmealtrack.presentation.receipt

import androidx.compose.runtime.Immutable
import com.example.qrmealtrack.presentation.model.ReceiptUiModel

@Immutable
data class ReceiptUiState(
    val statistics: Statistics = Statistics(),
    val webPageInfo: String? = null,
    val receiptsByDay: Map<String, List<ReceiptUiModel>> = emptyMap(),
    val totalsByDay: Map<String, Double> = emptyMap(),
    val expandedReceiptIds: Set<Long> = emptySet(),
)

data class Statistics(
    val minPrice: Double = 0.0,
    val maxPrice: Double = 0.0,
    val avgPrice: Double = 0.0,
    val avgPerDay: Double = 0.0,
    val avgPerWeek: Double = 0.0,
    val avgPerMonth: Double = 0.0
)