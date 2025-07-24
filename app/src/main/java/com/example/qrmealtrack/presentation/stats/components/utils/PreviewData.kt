package com.example.qrmealtrack.presentation.stats.components.utils

import com.example.qrmealtrack.presentation.stats.colors_provider.defaultCategoryColors
import com.example.qrmealtrack.presentation.stats.components.CategoryStat
import com.example.qrmealtrack.presentation.stats.components.LabelMode
import com.example.qrmealtrack.presentation.stats.model.toUiModels

object PreviewMocks {
    val categoryStats = listOf(
        CategoryStat("NO_CATEGORY", 120.0),
        CategoryStat("MEALS", 300.0),
        CategoryStat("CLOTHING", 180.0),
        CategoryStat("BEAUTY", 90.0),
        CategoryStat("TRANSPORT", 150.0),
        CategoryStat("GROCERIES", 250.0)
    )

    val categoryUiModels = categoryStats.toUiModels(
        defaultCategoryColors(),
        LabelMode.BOTH
    ).first
}