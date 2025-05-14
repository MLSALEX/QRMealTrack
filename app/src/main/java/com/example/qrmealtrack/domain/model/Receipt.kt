package com.example.qrmealtrack.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Receipt(
    val fiscalCode: String,
    val enterprise: String,
    val dateTime: Long,
    val type: String,
    val items: List<Meal>,
    val total: Double
)

@Immutable
data class Meal(
    val name: String,
    val weight: Double,
    val unitPrice: Double,
    val price: Double
)