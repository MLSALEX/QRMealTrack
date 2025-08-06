package com.example.qrmealtrack.presentation.trends

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.qrmealtrack.presentation.components.AppScaffold
import com.example.qrmealtrack.presentation.components.FilterType
import com.example.qrmealtrack.presentation.components.getDefaultCategories
import com.example.qrmealtrack.presentation.components.withScaffoldPadding
import com.example.qrmealtrack.presentation.trends.components.GranularityType
import com.example.qrmealtrack.presentation.trends.components.LineChart
import com.example.qrmealtrack.presentation.trends.components.TrendsControlBar
import com.example.qrmealtrack.presentation.trends.mock.mockDayPoints
import com.example.qrmealtrack.presentation.trends.mock.mockMonthPoints
import com.example.qrmealtrack.presentation.trends.mock.mockWeekPoints


@Composable
fun TrendsScreen() {
    AppScaffold { innerPadding ->
        RequireLandscapeOrientation()
        TrendsLandscapeContent(
            modifier = Modifier.withScaffoldPadding(innerPadding)
        )
    }
}

@Composable
private fun RequireLandscapeOrientation() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}

@Composable
fun TrendsLandscapeContent(modifier: Modifier = Modifier) {
    var selectedGranularity by remember { mutableStateOf(GranularityType.WEEK) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val chartPoints = remember(selectedGranularity) {
        when (selectedGranularity) {
            GranularityType.DAY -> mockDayPoints
            GranularityType.WEEK -> mockWeekPoints
            GranularityType.MONTH -> mockMonthPoints
        }
    }
    var filterState by remember { mutableStateOf(getDefaultCategories()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050A12))
            .padding(16.dp)
    ) {
        TrendsControlBar(
            filterState = filterState,
            onFilterChange = { filterState = it },
            onClearFilter = {
                filterState = FilterType.Categories(
                    filterState.categories.map { it.copy(isSelected = false) }.toSet()
                )
            },
            selectedGranularity = selectedGranularity,
            onGranularityChange = { selectedGranularity = it },
            onCalendarClick = { /* TODO: open date picker */ }
        )

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LineChart(
                points = chartPoints,
                selectedIndex = selectedIndex,
                onPointTap = { selectedIndex = it },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF050A12)
@Composable
fun TrendsScreenPreview() {
    MaterialTheme {
        TrendsScreen()
    }
}