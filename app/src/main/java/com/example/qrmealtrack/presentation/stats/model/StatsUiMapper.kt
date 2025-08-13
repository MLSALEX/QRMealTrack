package com.example.qrmealtrack.presentation.stats.model

import androidx.compose.ui.graphics.Color
import com.example.qrmealtrack.domain.model.StatsSummary
import com.example.qrmealtrack.presentation.stats.components.CategoryStat
import com.example.qrmealtrack.presentation.stats.components.LabelMode

fun StatsSummary.toUiModel(): StatsUiModel {
    return StatsUiModel(
        formattedWeight = "${totalWeight.format(3)} kg",
        formattedCost = "MDL ${totalCost.format(2)}",
    )
}

fun Double.format(digits: Int): String = "%.${digits}f".format(this)

fun List<CategoryStat>.toUiModels(
    colors: List<Color>,
    labelMode: LabelMode
): Pair<List<CategoryUiModel>, Int> {

    val total = sumOf { it.total }.toInt()

    val uiModels = mapIndexed { index, stat ->
        val roundedValue = stat.total.toInt()
        val percent = if (total > 0) ((roundedValue.toFloat() / total) * 100).toInt() else 0
        val color = colors.getOrElse(index) { Color.Gray }

        val labelText = when (labelMode) {
            LabelMode.PERCENT -> "$percent%"
            LabelMode.NAME -> stat.category
            LabelMode.BOTH -> "$percent% ${stat.category}"
        }

        CategoryUiModel(
            categoryName = stat.category,
            percent = percent,
            labelText = labelText,
            color = color,
            value = roundedValue
        )
    }
    return uiModels to total
}