package com.example.qrmealtrack.presentation.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.qrmealtrack.R
import com.example.qrmealtrack.presentation.model.ReceiptUiModel
import com.example.qrmealtrack.presentation.receipt.ReceiptListViewModel
import com.example.qrmealtrack.presentation.receipt.ReceiptUiAction
import com.example.qrmealtrack.presentation.receipt.ReceiptUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: ReceiptListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var receiptToDelete by remember { mutableStateOf<ReceiptUiModel?>(null) }

    // Подтверждение удаления
    receiptToDelete?.let { receipt ->
        AlertDialog(
            onDismissRequest = { receiptToDelete = null },
            title = { Text(stringResource(R.string.delete_receipt)) },
            text = { Text(stringResource(R.string.are_you_sure)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onAction(ReceiptUiAction.DeleteReceipt(receipt))
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

    // Содержимое
    HomeContent(
        state = state,
        onDeleteRequest = { receiptToDelete = it },
        onToggle = { viewModel.onAction(ReceiptUiAction.ToggleReceipt(it)) }
    )
}

@Composable
fun HomeContent(
    state: ReceiptUiState,
    onDeleteRequest: (ReceiptUiModel) -> Unit,
    onToggle: (Long) -> Unit
) {
    val receiptsByDay = remember(state.receiptsByDay) {
        state.receiptsByDay.toSortedMap(compareByDescending { dateStr ->
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(dateStr)?.time ?: 0L
        })
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        receiptsByDay.forEach { (day, receipts) ->
            item(key = "header_$day") {
                ReceiptHeader(day = day, total = receipts.sumOf { it.total })
            }
            items(receipts, key = { it.id }) { receipt ->
                val isExpanded = state.expandedReceiptIds.contains(receipt.id)
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

@Composable
fun ReceiptHeader(day: String, total: Double) {
    Text(
        text = buildString {
            append(if (isToday(day)) "Today" else day)
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onCardToggle()
                },
                onLongClick = {
                    onLongClick()
                }
            ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize()
        ) {
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

fun isToday(dateStr: String): Boolean {
    val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val today = format.format(Date())
    return dateStr == today
}
