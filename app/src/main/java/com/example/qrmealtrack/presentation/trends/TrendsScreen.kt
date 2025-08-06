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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.qrmealtrack.presentation.components.AppScaffold
import com.example.qrmealtrack.presentation.components.FilterType
import com.example.qrmealtrack.presentation.components.getDefaultCategories
import com.example.qrmealtrack.presentation.components.withScaffoldPadding
import com.example.qrmealtrack.presentation.trends.components.GranularityType
import com.example.qrmealtrack.presentation.trends.components.LineChart
import com.example.qrmealtrack.presentation.trends.components.MultiLineChart
import com.example.qrmealtrack.presentation.trends.components.TrendsControlBar
import com.example.qrmealtrack.presentation.trends.model.UiChartPoint
import com.example.qrmealtrack.presentation.trends.state.TrendsUiState
import com.example.qrmealtrack.presentation.trends.viewmodel.TrendsViewModel
import com.example.qrmealtrack.presentation.utils.CategoryColorProvider

@Composable
fun TrendsScreen(viewModel: TrendsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    AppScaffold { innerPadding ->
        RequireLandscapeOrientation()
        TrendsLandscapeContent(
            modifier = Modifier.withScaffoldPadding(innerPadding),
            state = state,
            onFilterChange = viewModel::onFilterChange,
            onClearFilter = viewModel::clearFilter,
            onGranularityChange = viewModel::onGranularityChange
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
fun TrendsLandscapeContent(
    modifier: Modifier = Modifier,
    state: TrendsUiState,
    onFilterChange: (FilterType.Categories) -> Unit,
    onClearFilter: () -> Unit,
    onGranularityChange: (GranularityType) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var selectedOffset by remember { mutableStateOf<Offset?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050A12))
            .padding(16.dp)
    ) {
        TrendsControlBar(
            filterState = state.filter,
            onFilterChange = onFilterChange,
            onClearFilter = onClearFilter,
            selectedGranularity = state.granularity,
            onGranularityChange = onGranularityChange,
            onCalendarClick = { /* TODO */ }
        )

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            MultiLineChart(
                groupedPoints = state.groupedPoints,
                selectedCategory = selectedCategory,
                selectedIndex = selectedIndex,
                selectedOffset = selectedOffset,
                onPointTap = { category, index, offset ->
                    selectedCategory = category
                    selectedIndex = index
                    selectedOffset = offset
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF050A12,
    widthDp = 800,
    heightDp = 480
)
@Composable
fun TrendsLandscapePreview() {
    val mockCategories = getDefaultCategories()
    val selectedCategories = mockCategories.categories.map {
        if (it.key == "meals" || it.key == "transport") it.copy(isSelected = true) else it
    }.toSet()

    val filter = FilterType.Categories(selectedCategories)
    val colorProvider = CategoryColorProvider()

    val points = listOf(
        UiChartPoint("meals", 120f, "Aug 01", colorProvider.getColorForCategory("meals")),
        UiChartPoint("transport", 90f, "Aug 01", colorProvider.getColorForCategory("transport")),
        UiChartPoint("meals", 140f, "Aug 02", colorProvider.getColorForCategory("meals")),
        UiChartPoint("transport", 80f, "Aug 02", colorProvider.getColorForCategory("transport")),
        UiChartPoint("meals", 160f, "Aug 03", colorProvider.getColorForCategory("meals")),
        UiChartPoint("transport", 110f, "Aug 03", colorProvider.getColorForCategory("transport"))
    )

    val groupedPoints = points.groupBy { it.category }

    val mockState = TrendsUiState(
        filter = filter,
        granularity = GranularityType.DAY,
        groupedPoints = groupedPoints
    )

    MaterialTheme {
        TrendsLandscapeContent(
            state = mockState,
            modifier = Modifier.fillMaxSize(),
            onFilterChange = {},
            onClearFilter = {},
            onGranularityChange = {}
        )
    }
}

