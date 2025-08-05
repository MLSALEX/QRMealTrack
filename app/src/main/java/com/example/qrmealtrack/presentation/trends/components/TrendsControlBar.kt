package com.example.qrmealtrack.presentation.trends.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TrendsControlBar(
    selectedCategory: String = "Meal",
    onCategorySelected: (String) -> Unit = {},
    selectedGranularity: GranularityType,
    onGranularityChange: (GranularityType) -> Unit,
    onCalendarClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // ✅ Dropdown категорий
        CategoryDropdown(
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected
        )

        // ✅ Переключатель гранулярности
        NeonSegmentedButton(
            selected = selectedGranularity,
            onGranularityChange = onGranularityChange,
        )

        // ✅ Иконка календаря
        IconButton(onClick = onCalendarClick) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Select period",
                tint = Color.Cyan
            )
        }
    }
}