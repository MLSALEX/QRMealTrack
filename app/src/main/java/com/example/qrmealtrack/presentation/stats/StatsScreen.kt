package com.example.qrmealtrack.presentation.stats

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.qrmealtrack.R
import com.example.qrmealtrack.domain.model.PriceChangeItem
import com.example.qrmealtrack.domain.usecase.StatsSummary
import com.example.qrmealtrack.presentation.stats.colors_provider.defaultCategoryColors
import com.example.qrmealtrack.presentation.stats.components.DonutChartWithCenterButton
import com.example.qrmealtrack.presentation.stats.components.LabelMode
import com.example.qrmealtrack.presentation.stats.model.CategoryUiModel
import com.example.qrmealtrack.presentation.stats.model.format
import com.example.qrmealtrack.ui.theme.stats.StatsTheme
import com.example.qrmealtrack.ui.theme.stats.glassGlowBackground

@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val labelMode by viewModel.labelMode.collectAsState()

    StatsScreenContent(
        state = state,
        labelMode = labelMode,
        onModeChange = viewModel::toggleLabelMode,
        onFilterSelected = remember { viewModel::onFilterSelected }
    )
}

@Composable
fun StatsScreenContent(
    state: StatsUiState,
    labelMode: LabelMode,
    onModeChange: () -> Unit,
    onFilterSelected: (TimeFilter) -> Unit
) {
    val model = state.uiModel
    val colors = StatsTheme.colors

    val chartPadding = 16.dp
    val chartSize = 200.dp
    val chartSpacer = 24.dp

    StatsTheme {
        Column {
            TimeFilterRow(
                selected = state.selectedFilter,
                onSelect = onFilterSelected
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                item {
                    StatsGrid(
                        weight = model.formattedWeight,
                        cost = model.formattedCost,
                        topDish = model.topDish,
                        topDishCost = model.formattedTopDishCost,
                        priceChanges = model.priceChanges,
                        priceUpCount = model.priceUpCount,
                        priceDownCount = model.priceDownCount,
                        priceDynamics = model.priceDynamics
                    )
                    Spacer(Modifier.height(chartSpacer))
                }

                item {
                    if (state.categoryUiModels.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(chartPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            DonutChartWithCenterButton(
                                categories = state.categoryUiModels,
                                labelMode = labelMode,
                                onModeChange = onModeChange,
                                modifier = Modifier
                                    .padding(chartPadding)
                                    .size(chartSize)
                            )
                        }
                    } else {
                        Text(
                            text = "Нет данных за выбранный период",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun TimeFilterRow(
    selected: TimeFilter,
    onSelect: (TimeFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = StatsTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .glassGlowBackground(
                glowRed = colors.glowRed,
                glowBlue = colors.glowBlue,
                backgroundColor = colors.cardBackground,
                shape = RectangleShape,
                glowAlpha = 0.5f
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeFilter.entries.forEach { filter ->
            FilterButton(
                label = filter.label,
                selected = selected == filter,
                onClick = { onSelect(filter) }
            )
        }

        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "Custom Date",
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(24.dp),
            tint = colors.textSecondary
        )
    }
}

@Composable
fun FilterButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = StatsTheme.colors
    Text(
        text = label,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .clickable { onClick() },
        style = MaterialTheme.typography.labelMedium,
        color = if (selected) colors.textPrimary else colors.textSecondary
    )
}

enum class TimeFilter(val label: String) {
    Today("Today"),
    Week("Week"),
    Month("Month"),
    All("All Time")
}

@Composable
fun StatsGrid(
    weight: String,
    cost: String,
    topDish: String,
    topDishCost: String,
    priceChanges: Int,
    priceUpCount: Int,
    priceDownCount: Int,
    priceDynamics: List<PriceChangeItem>
) {
    val weightIcon = painterResource(id = R.drawable.kitchen_scale)
    val costIcon = painterResource(id = R.drawable.money_bag)
    val dishIcon = painterResource(id = R.drawable.plate)
    val chartIcon = painterResource(id = R.drawable.chart)
    Column(Modifier.padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                title = "Weight",
                value = weight,
                icon = weightIcon,
                modifier = Modifier.weight(1f)
            )
            StatCard(title = "Cost", value = cost, icon = costIcon, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Top Dish",
                value = topDish,
                subtitle = topDishCost,
                icon = dishIcon,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Price Changes",
                value = "$priceChanges\n↑ $priceUpCount ↓ $priceDownCount",
                icon = chartIcon,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(16.dp))
//        PriceDynamicsCard(items = priceDynamics)
    }
}

@Composable
fun BaseStatCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = StatsTheme.colors
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        label = "PressScale"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
            .glassGlowBackground(
                glowRed = colors.glowRed,
                glowBlue = colors.glowBlue,
                backgroundColor = colors.cardBackground
            )
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            content = content
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String? = null,
    icon: Painter? = null,
    onClick: () -> Unit = {}
) {
    val colors = StatsTheme.colors

    BaseStatCard(
        modifier = modifier.height(140.dp),
        onClick = onClick
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary
            )
            icon?.let {
                Icon(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 4.dp),
                    tint = colors.textSecondary
                )
            }
        }

        Column {
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textPrimary
            )
            subtitle?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@Composable
fun PriceDynamicsCard(
    items: List<PriceChangeItem>,
    modifier: Modifier = Modifier
) {
    val colors = StatsTheme.colors

    BaseStatCard(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
    ) {
        Text(
            text = "Price Dynamics",
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (items.isEmpty()) {
            Text("No recent changes", style = MaterialTheme.typography.bodySmall)
        } else {
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        item.dishName,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val icon =
                            if (item.isIncreased) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
                        val color = if (item.isIncreased) Color.Red else Color(0xFF2E7D32)

                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "${item.difference.format(2)} MDL",
                            style = MaterialTheme.typography.bodySmall,
                            color = color
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatsScreenPr() {
    StatsTheme {
        StatsScreenContent(
            state = StatsUiState(
                summary = StatsSummary(
                    totalWeight = 3.45,
                    totalCost = 212.75,
                    topDish = "Pizza Quattro Formaggi",
                    topDishCost = 89.99,
                    priceChanges = 7,
                    priceUpCount = 4,
                    priceDownCount = 3
                ),
                priceDynamics = listOf(
                    PriceChangeItem("Pasta", true, 3.0),
                    PriceChangeItem("Burger", false, 1.2)
                ),
                selectedFilter = TimeFilter.Week,
                // ✅ подставим примерные категории для превью
                categoryUiModels = listOf(
                    CategoryUiModel(
                        categoryName = "MEALS",
                        percent = 50,
                        labelText = "50% MEALS",
                        color = defaultCategoryColors()[1],
                        value = 100
                    ),
                    CategoryUiModel(
                        categoryName = "CLOTHING",
                        percent = 30,
                        labelText = "30% CLOTHING",
                        color = defaultCategoryColors()[2],
                        value = 60
                    ),
                    CategoryUiModel(
                        categoryName = "BEAUTY",
                        percent = 20,
                        labelText = "20% BEAUTY",
                        color = defaultCategoryColors()[3],
                        value = 40
                    )
                ),
                totalCategoryValue = 200
            ),
            labelMode = LabelMode.BOTH,
            onModeChange = {},
            onFilterSelected = {}
        )
    }
}
