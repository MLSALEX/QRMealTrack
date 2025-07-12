package com.example.qrmealtrack.presentation.stats.model

import com.example.qrmealtrack.domain.model.PriceChangeItem
import com.example.qrmealtrack.domain.usecase.StatsSummary

fun StatsSummary.toUiModel(
    priceDynamics: List<PriceChangeItem>
): StatsUiModel {
    return StatsUiModel(
        formattedWeight = "${totalWeight.format(3)} kg",
        formattedCost = "MDL ${totalCost.format(2)}",
        topDish = topDish ?: "–",
        formattedTopDishCost = "MDL ${topDishCost.format(2)}",
        priceChanges = priceChanges,
        priceUpCount = priceUpCount,
        priceDownCount = priceDownCount,
        priceDynamics = priceDynamics
    )
}

// утилита форматирования
fun Double.format(digits: Int): String = "%.${digits}f".format(this)