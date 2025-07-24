package com.example.qrmealtrack.presentation.stats.colors_provider

import androidx.compose.ui.graphics.Color

fun defaultCategoryColors(): List<Color> = listOf(
    Color.Gray,        // NO_CATEGORY
    Color(0xFF2196F3), // MEALS
    Color(0xFF9C27B0), // CLOTHING
    Color(0xFFE91E63), // BEAUTY
    Color(0xFFFF9800), // TRANSPORT
    Color(0xFF4CAF50)  // GROCERIES
)

fun Color.toAndroidColor(): Int =
    android.graphics.Color.argb(
        (alpha * 255).toInt(),
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )