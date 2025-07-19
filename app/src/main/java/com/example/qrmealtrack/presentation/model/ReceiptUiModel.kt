package com.example.qrmealtrack.presentation.model

import androidx.compose.runtime.Immutable
import com.example.qrmealtrack.domain.model.ReceiptCategory

@Immutable
data class ReceiptUiModel(
    val id: Long,
    val fiscalCode: String,
    val enterprise: String,
    val dateTime: Long,
    val date: String,
    val items: List<ItemUiModel>,
    val total:  Double,
    val isToday: Boolean,
    val category: ReceiptCategory
)

@Immutable
data class ItemUiModel(
    val name: String,
    val weight: String,
    val unitPrice: String,
    val price: String,
    val categoryKey: String,
    val isWeightBased: Boolean = false
)