package com.example.qrmealtrack.presentation.home

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.qrmealtrack.R
import com.example.qrmealtrack.data.mapper.toUiModel
import com.example.qrmealtrack.presentation.ReceiptListViewModel
import com.example.qrmealtrack.presentation.model.ReceiptUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: ReceiptListViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val homeState by homeViewModel.state.collectAsState()

    val receiptsByDay = remember(state.receiptsByDay) {
        state.receiptsByDay.toSortedMap(compareByDescending { dateStr ->
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(dateStr)?.time ?: 0L
        })
    }
    var receiptToDelete by remember { mutableStateOf<ReceiptUiModel?>(null) }

    // Диалог подтверждения удаления
    receiptToDelete?.let { receipt ->
        AlertDialog(
            onDismissRequest = { receiptToDelete = null },
            title = { Text(stringResource(R.string.delete_receipt)) },
            text = { Text(stringResource(R.string.are_you_sure)) },
            confirmButton = {
                TextButton(onClick = {
                    homeViewModel.deleteReceipt(receipt)
                    receiptToDelete = null
                }) {
                    Text("Ok")
                }
            },
            dismissButton = {
                TextButton(onClick = { receiptToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }


    LaunchedEffect(Unit) {
        Log.d("📱UI", "HomeScreen: ${state.receiptsByDay.size} дней")
        state.receiptsByDay.forEach { (date, list) ->
            Log.d("🧾", "$date: ${list.size} чеков")
        }
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        receiptsByDay.forEach { (day, receipts) ->
            val totalForDay = receipts.sumOf { it.total.replace(",", ".").toDouble() }
            item(key = "header_$day") {
                Text(
                    text = buildString {
                        append(if (isToday(day)) "Сегодня" else day)
                        append(" — total: %.2f MDL".format(totalForDay))
                    },
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            items(
                items = receipts,
                key = { it.id }
            ) { receipt ->
                ReceiptCard(receipt = receipt, onLongClick = {
                    receiptToDelete = receipt
                })
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReceiptCard(receipt: ReceiptUiModel, onLongClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongClick() }
                )
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Date: ${receipt.date}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            receipt.items.forEach { meal ->
                Text(
                    text = "${meal.name}: ${meal.weight} × ${meal.unitPrice} = ${meal.price}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = "Total: ${receipt.total}",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

fun isToday(dateStr: String): Boolean {
    val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val today = format.format(Date())
    return dateStr == today
}
