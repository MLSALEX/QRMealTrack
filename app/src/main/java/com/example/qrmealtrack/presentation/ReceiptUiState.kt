package com.example.qrmealtrack.presentation

import androidx.compose.runtime.Immutable
import com.example.qrmealtrack.data.local.ReceiptEntity
import com.example.qrmealtrack.domain.model.Receipt

@Immutable
data class ReceiptUiState(
    val receipts: List<ReceiptEntity> = emptyList(),
    val statistics: Statistics = Statistics(),
    val webPageInfo: String? = null,
    val receiptsByDay: Map<String, List<Receipt>> = emptyMap(),
    val totalsByDay: Map<String, Double> = emptyMap()
)

data class Statistics(
    val minPrice: Double = 0.0,
    val maxPrice: Double = 0.0,
    val avgPrice: Double = 0.0,
    val avgPerDay: Double = 0.0,
    val avgPerWeek: Double = 0.0,
    val avgPerMonth: Double = 0.0
)