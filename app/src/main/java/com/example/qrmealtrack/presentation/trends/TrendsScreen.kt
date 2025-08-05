package com.example.qrmealtrack.presentation.trends

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.qrmealtrack.presentation.components.AppScaffold
import com.example.qrmealtrack.presentation.components.withScaffoldPadding
import com.example.qrmealtrack.presentation.trends.components.GranularityType
import com.example.qrmealtrack.presentation.trends.components.LineChart
import com.example.qrmealtrack.presentation.trends.components.TrendsControlBar
import com.example.qrmealtrack.presentation.trends.mock.mockDayPoints
import com.example.qrmealtrack.presentation.trends.mock.mockMonthPoints
import com.example.qrmealtrack.presentation.trends.mock.mockWeekPoints
import androidx.compose.ui.platform.LocalContext
import android.content.pm.ActivityInfo

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050A12))
            .padding(16.dp)
    ) {
            TrendsControlBar(
            selectedCategory = "Meal",
            onCategorySelected = { /* TODO: handle category change */ },
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