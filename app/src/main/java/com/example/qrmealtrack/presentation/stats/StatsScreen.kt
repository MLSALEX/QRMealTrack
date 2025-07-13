package com.example.qrmealtrack.presentation.stats

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.qrmealtrack.R
import com.example.qrmealtrack.domain.model.PriceChangeItem
import com.example.qrmealtrack.domain.usecase.StatsSummary
import com.example.qrmealtrack.presentation.stats.model.format
import com.example.qrmealtrack.ui.theme.stats.StatsTheme
import com.example.qrmealtrack.ui.theme.stats.StatsTheme.colors
import com.example.qrmealtrack.ui.theme.stats.glassGlowBackground

@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    StatsScreenContent(
        state = state,
        onFilterSelected = remember { viewModel::onFilterSelected }
    )
}

@Composable
fun StatsScreenContent(
    state: StatsUiState,
    onFilterSelected: (TimeFilter) -> Unit
) {
    val model = state.uiModel
    val colors = StatsTheme.colors

    StatsTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            TimeFilterRow(
                selected = state.selectedFilter,
                onSelect = onFilterSelected
            )

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
        TimeFilter.values().forEach { filter ->
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
            StatCard(title = "Weight", value = weight, icon = weightIcon, modifier = Modifier.weight(1f))
            StatCard(title = "Cost", value = cost, icon = costIcon, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(title = "Top Dish", value = topDish, subtitle = topDishCost, icon = dishIcon,modifier = Modifier.weight(1f))
            StatCard(title = "Price Changes",
                value = "$priceChanges\n↑ $priceUpCount ↓ $priceDownCount",
                icon = chartIcon,
                modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        PriceDynamicsCard(items = priceDynamics)
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
                        val icon = if (item.isIncreased) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
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
                    PriceChangeItem("Burger", false,1.2)
                ),
                selectedFilter = TimeFilter.Week
            ),
            onFilterSelected = {} // no-op stub
        )
    }
}