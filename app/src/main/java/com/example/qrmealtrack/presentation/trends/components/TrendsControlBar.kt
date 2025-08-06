package com.example.qrmealtrack.presentation.trends.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.qrmealtrack.presentation.components.CategoryFilterDropdown
import com.example.qrmealtrack.presentation.components.FilterType

@Composable
fun TrendsControlBar(
    filterState: FilterType.Categories,
    onFilterChange: (FilterType.Categories) -> Unit,
    onClearFilter: () -> Unit,
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
        CategoryFilterDropdown(
            title = "Categories",
            filterType = filterState,
            onToggle = onFilterChange,
            onClearFilter = onClearFilter,
            modifier = Modifier.weight(1f)
        )

        NeonSegmentedButton(
            selected = selectedGranularity,
            onGranularityChange = onGranularityChange,
        )

        IconButton(onClick = onCalendarClick) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Select period",
                tint = Color.Cyan
            )
        }
    }
}