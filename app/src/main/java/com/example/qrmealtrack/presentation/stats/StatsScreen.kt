package com.example.qrmealtrack.presentation.stats

// Vico Core
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.qrmealtrack.R


@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val state by viewModel.statsState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TimeFilterRow(selected = selectedFilter, onSelect = viewModel::onFilterSelected)

        StatsGrid(
            weight = "${state.totalWeight.format(3)} kg",
            cost = "MDL ${state.totalCost.format(2)}",
            topDish = state.topDish ?: "–",
            topDishCost = "MDL${state.topDishCost.format(2)}",
            priceChanges = state.priceChanges
        )
    }
}

fun Double.format(digits: Int): String = "%.${digits}f".format(this)

@Composable
fun TimeFilterRow(
    selected: TimeFilter,
    onSelect: (TimeFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(24.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
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
            tint = Color.Black
        )
    }
}

@Composable
fun FilterButton(label: String, selected: Boolean, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            containerColor = if (selected) Color(0xFF2979FF) else Color.Transparent,
            contentColor = if (selected) Color.White else Color.Black
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(text = label)
    }
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
    priceChanges: Int
) {
    val weightIcon = painterResource(id = R.drawable.kitchen_scale)
    val costIcon = painterResource(id = R.drawable.money_bag)
    val dishIcon = painterResource(id = R.drawable.plate)
//    val chartIcon = painterResource(id = R.drawable.ic_chart)
    Column(Modifier.padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(title = "Weight", value = weight, icon = weightIcon, modifier = Modifier.weight(1f))
            StatCard(title = "Cost", value = cost, icon = costIcon, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(title = "Top Dish", value = topDish, subtitle = topDishCost, icon = dishIcon)
            StatCard(title = "Price Changes", value = priceChanges.toString())
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String? = null,
    icon: Painter? = null
) {
    Card(
        modifier = modifier
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                icon?.let {
                    Icon(
                        painter = it,
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(end = 4.dp),
                        tint = Color.Gray
                    )
                }
            }

            Column {
                Text(value, style = MaterialTheme.typography.headlineSmall)
                subtitle?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                }
            }
        }
    }
}
