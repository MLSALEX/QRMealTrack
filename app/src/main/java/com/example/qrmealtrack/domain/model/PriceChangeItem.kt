package com.example.qrmealtrack.domain.model

data class PriceChangeItem(
    val dishName: String,
    val isIncreased: Boolean,
    val difference: Double
)