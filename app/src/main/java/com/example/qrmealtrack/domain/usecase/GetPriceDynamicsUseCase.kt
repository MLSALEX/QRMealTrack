package com.example.qrmealtrack.domain.usecase

data class PriceChangeItem(
    val dishName: String,
    val isIncreased: Boolean,
    val difference: Double
)