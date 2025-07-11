package com.example.qrmealtrack.domain.model

data class Receipt(
    val id: Long,
    val fiscalCode: String,
    val enterprise: String,
    val dateTime: Long,
    val type: String,
    val items: List<Meal>,
    val total: Double
)

data class Meal(
    val name: String,
    val weight: Double,
    val unitPrice: Double,
    val price: Double,
    val isWeightBased: Boolean = false,
    val category: String? = null
)