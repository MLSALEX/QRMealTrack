package com.example.qrmealtrack.presentation.home

import android.text.format.DateUtils.isToday
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.qrmealtrack.R
import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.presentation.ReceiptListViewModel
import com.example.qrmealtrack.presentation.components.TopBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(viewModel: ReceiptListViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    val receiptsByDay = remember(state.receiptsByDay) {
        state.receiptsByDay.toSortedMap(compareByDescending { dateStr ->
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(dateStr)?.time ?: 0L
        })
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
            item {
                Text(
                    text = if (isToday(day)) "Сегодня" else day,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            items(receipts) { receipt ->
                ReceiptCard(receipt)
            }
        }
    }
}
@Composable
fun ReceiptCard(receipt: Receipt) {
    val formattedDate = remember(receipt.dateTime) {
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            .format(Date(receipt.dateTime))
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Date: $formattedDate",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            receipt.items.forEach { meal ->
                Text(
                    text = "${meal.name}: ${meal.weight} × ${"%.2f".format(meal.unitPrice)} = ${"%.2f".format(meal.price)}",
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
