package com.example.qrmealtrack.presentation.model

import androidx.compose.runtime.Immutable
import com.example.qrmealtrack.presentation.components.CategoryUi

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
    val category: CategoryUi? = null
)

@Immutable
data class ItemUiModel(
    val name: String,
    val weight: String,
    val unitPrice: String,
    val price: String,
    val category: String? = null,
    val isWeightBased: Boolean = false
)