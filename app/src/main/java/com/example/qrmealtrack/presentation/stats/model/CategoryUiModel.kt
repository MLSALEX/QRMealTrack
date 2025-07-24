package com.example.qrmealtrack.presentation.stats.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class CategoryUiModel(
    val categoryName: String,
    val percent: Int,
    val labelText: String,
    val color: Color,
    val value: Int
)