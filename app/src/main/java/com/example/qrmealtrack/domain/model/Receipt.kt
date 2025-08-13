package com.example.qrmealtrack.domain.model

data class Receipt(
    val id: Long,
    val fiscalCode: String,
    val enterprise: String,
    val dateTime: Long,
    val type: String,
    val items: List<ReceiptItem>,
    val total: Double,
    val category: ReceiptCategory,
)

data class ReceiptItem(
    val name: String,
    val weight: Double,
    val unitPrice: Double,
    val price: Double,
    val isWeightBased: Boolean = false,
    val category: ReceiptCategory = ReceiptCategory.NO_CATEGORY
)