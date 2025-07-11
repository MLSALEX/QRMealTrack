package com.example.qrmealtrack.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class ReceiptUiModel(
    val id: Long,
    val fiscalCode: String,
    val dateTime: Long,
    val date: String,
    val items: List<MealUiModel>,
    val total:  Double
)

@Immutable
data class MealUiModel(
    val name: String,
    val weight: String,
    val unitPrice: String,
    val price: String,
    val category: String? = null,
    val isWeightBased: Boolean = false
)