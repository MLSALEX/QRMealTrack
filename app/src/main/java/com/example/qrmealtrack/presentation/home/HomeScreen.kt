package com.example.qrmealtrack.presentation.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.qrmealtrack.R
import com.example.qrmealtrack.presentation.components.CategoryFilterDropdown
import com.example.qrmealtrack.presentation.components.FilterType
import com.example.qrmealtrack.presentation.components.getDefaultCategories
import com.example.qrmealtrack.presentation.model.MealUiModel
import com.example.qrmealtrack.presentation.model.ReceiptUiModel
import com.example.qrmealtrack.presentation.receipt.ReceiptListViewModel
import com.example.qrmealtrack.presentation.receipt.ReceiptUiAction
import com.example.qrmealtrack.ui.theme.QRMealTrackTheme
import com.example.qrmealtrack.ui.theme.home.receiptCardGlowBackground

@Composable
fun HomeScreen(
    listViewModel: ReceiptListViewModel = hiltViewModel(),
    filterViewModel: HomeFilterViewModel = hiltViewModel()
) {
    val state by listViewModel.state.collectAsState()
    val filterState by filterViewModel.filterState.collectAsState()

    var receiptToDelete by remember { mutableStateOf<ReceiptUiModel?>(null) }

    // Подтверждение удаления
    receiptToDelete?.let { receipt ->
        AlertDialog(
            onDismissRequest = { receiptToDelete = null },
            title = { Text(stringResource(R.string.delete_receipt)) },
            text = { Text(stringResource(R.string.are_you_sure)) },
            confirmButton = {
                TextButton(onClick = {
                    listViewModel.onAction(ReceiptUiAction.DeleteReceipt(receipt))
                    receiptToDelete = null
                }) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { receiptToDelete = null }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }
    // Фильтруем чеки на основе выбранных категорий
    val selectedNames = filterState.getSelectedNames()
    val filteredReceipts = if (selectedNames.isEmpty()) {
        state.receiptsByDay
    } else {
        state.receiptsByDay.mapValues { (_, receipts) ->
            receipts.filter { it.items.any { meal -> meal.category in selectedNames } }
        }.filterValues { it.isNotEmpty() }
    }

    HomeContent(
        receipts = filteredReceipts,
        expandedIds = state.expandedReceiptIds,
        filterState = filterState,
        onFilterChange = { filterViewModel.updateFilter(it) },
        onClearFilter = { filterViewModel.clearFilter() },
        onDeleteRequest = { receiptToDelete = it },
        onToggle = { listViewModel.onAction(ReceiptUiAction.ToggleReceipt(it)) }
    )
}

@Composable
fun HomeContent(
    receipts: Map<String, List<ReceiptUiModel>>,
    expandedIds: Set<Long>,
    filterState: FilterType.Categories,
    onFilterChange: (FilterType.Categories) -> Unit,
    onClearFilter: () -> Unit,
    onDeleteRequest: (ReceiptUiModel) -> Unit,
    onToggle: (Long) -> Unit
) {
    var selectedCategories by remember { mutableStateOf(getDefaultCategories()) }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CategoryFilterDropdown(
            title = "All Categories",
            filterType = filterState,
            onToggle = onFilterChange,
            onClearFilter = onClearFilter,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )


        Spacer(modifier = Modifier.height(12.dp))

        // ✅ получаем выбранные категории
        val selectedNames = selectedCategories.getSelectedNames()

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            receipts.forEach { (day, receipts) ->
                val totalForDay = receipts.sumOf { it.total }
                val isTodayHeader = receipts.any { it.isToday }

                item(key = "header_$day") {
                    ReceiptHeader(
                        day = day,
                        total = totalForDay,
                        isToday = isTodayHeader
                    )
                }

                items(receipts, key = { it.id }) { receipt ->
                    val isExpanded = expandedIds.contains(receipt.id)
                    Log.d("🔍", "Receipt ID: ${receipt.id} expanded=${isExpanded}")
                    ReceiptCard(
                        receipt = receipt,
                        isExpanded = isExpanded,
                        onCardToggle = { onToggle(receipt.id) },
                        onLongClick = { onDeleteRequest(receipt) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReceiptHeader(
    day: String,
    total: Double,
    isToday: Boolean
) {
    Text(
        text = buildString {
            append(if (isToday) "Today" else day)
            append(" — total: %.2f MDL".format(total))
        },
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReceiptCard(
    receipt: ReceiptUiModel,
    isExpanded: Boolean,
    onCardToggle: () -> Unit,
    onLongClick: () -> Unit
) {
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "Arrow Rotation"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))  // ✅ обрезаем всё содержимое и ripple по углам
            .receiptCardGlowBackground(
                glowPrimary = Color(0xFF00FFB0),
                glowSecondary = Color(0xFF00D4FF),
                backgroundColor = Color(0xCC121C2E), // чуть менее прозрачный фон
                glowAlpha = 0.5f
            )
            .combinedClickable(
                onClick = onCardToggle,
                onLongClick = onLongClick
            )
            .animateContentSize()
            .padding(16.dp)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurface
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = receipt.enterprise,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_drop_down),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(arrowRotation)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(
                    visible = isExpanded
                ) {
                    Column {
                        receipt.items.forEach { meal ->
                            Text(
                                text = "${meal.name}: ${meal.weight} × ${meal.unitPrice} = ${meal.price}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                Row (modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween){
                    Text(
                        text = "Total: ${receipt.total}",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = receipt.date,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Sci-Fi Receipt Card",
    showBackground = true,
    backgroundColor = 0xFF0A0F1C
)
@Composable
fun SciFiReceiptCardPreview() {
    QRMealTrackTheme {
// Пример списка покупок
        val sampleMeals = listOf(
            MealUiModel(
                name = "Burger Deluxe",
                weight = "250g",
                unitPrice = "5.99",
                price = "5.99",
                category = "Fast Food",
                isWeightBased = false
            ),
            MealUiModel(
                name = "French Fries",
                weight = "150g",
                unitPrice = "2.49",
                price = "2.49",
                category = "Snack",
                isWeightBased = false
            ),
            MealUiModel(
                name = "Coca-Cola",
                weight = "0.5L",
                unitPrice = "1.50",
                price = "1.50",
                category = "Drink",
                isWeightBased = false
            )
        )

        // Пример чека
        val sampleReceipt = ReceiptUiModel(
            id = 1L,
            fiscalCode = "ABC123456",
            enterprise = "Cyber Food Market",
            dateTime = System.currentTimeMillis(),
            date = "15.07.2025",
            items = sampleMeals,
            total = 9.98,
            isToday = true
        )

        // Отображаем sci-fi карточку
        ReceiptCard(
            receipt = sampleReceipt,
            isExpanded = true, // развернутая карточка
            onCardToggle = {},
            onLongClick = {}
        )
    }
}
