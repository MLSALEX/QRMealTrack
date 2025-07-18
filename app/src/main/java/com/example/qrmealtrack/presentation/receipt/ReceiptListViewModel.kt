package com.example.qrmealtrack.presentation.receipt

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrmealtrack.data.local.ReceiptEntity
import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import com.example.qrmealtrack.domain.usecase.FetchWebPageInfoUseCase
import com.example.qrmealtrack.domain.usecase.GetReceiptsGroupedByDayUseCase
import com.example.qrmealtrack.domain.usecase.SaveParsedReceiptUseCase
import com.example.qrmealtrack.presentation.model.ReceiptUiMapper
import com.example.qrmealtrack.presentation.utils.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceiptListViewModel @Inject constructor(
    private val repository: ReceiptRepository,
    private val fetchWebPageInfoUseCase: FetchWebPageInfoUseCase,
    private val saveParsedReceiptUseCase: SaveParsedReceiptUseCase,
    private val getReceiptsGroupedByDayUseCase: GetReceiptsGroupedByDayUseCase,
    private val uiMapper: ReceiptUiMapper,
    private val dateFormatter: DateFormatter
) :
    ViewModel() {
    private val _state = MutableStateFlow(ReceiptUiState())
    val state = _state.asStateFlow()

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    init {
        viewModelScope.launch {
            getReceiptsGroupedByDayUseCase().collect { result ->

                val mapped = result.receiptsByDay.mapValues { (_, list) ->
                    list.map { uiMapper.map(it) }
                }

                // 2. Сортировка по убыванию даты (ключ "dd.MM.yyyy")
                val sortedReceipts = mapped.toSortedMap(compareByDescending { dayStr ->
                    dateFormatter.parseDay(dayStr)?.time ?: 0L
                })

                _state.update {
                    it.copy(
                        receiptsByDay = sortedReceipts,
                        totalsByDay = result.totalsByDay,
                        // если хочешь посчитать stats:
                        statistics = calculateStats(result.receiptsByDay.values.flatten())
                    )
                }
            }
        }
    }
    fun onAction(action: ReceiptUiAction) {
        when (action) {
            is ReceiptUiAction.ToggleReceipt -> {
                _state.update { current ->
                    val updated = if (current.expandedReceiptIds.contains(action.id))
                        current.expandedReceiptIds - action.id
                    else
                        current.expandedReceiptIds + action.id
                    Log.d("🔁", "Toggle ID ${action.id}, new expanded = $updated")
                    current.copy(expandedReceiptIds = updated)
                }
            }

            is ReceiptUiAction.DeleteReceipt -> {
                viewModelScope.launch {
                    repository.deleteReceiptGroup(action.receipt.fiscalCode, action.receipt.dateTime)
                }
            }

            is ReceiptUiAction.SaveParsed -> {
                viewModelScope.launch {
                    val result = saveParsedReceiptUseCase(action.parsed)
                    _message.emit(
                        if (result.isSuccess) "Чек добавлен"
                        else result.exceptionOrNull()?.message ?: "Ошибка"
                    )
                }
            }

            is ReceiptUiAction.FetchWebPageInfo -> {
                viewModelScope.launch {
                    val result = fetchWebPageInfoUseCase(action.url)
                    _state.update { it.copy(webPageInfo = result) }
                }
            }
        }
    }

    private fun calculateStats(receipts: List<Receipt>): Statistics {
        val prices = receipts.map { it.total }
        val minPrice = prices.minOrNull() ?: 0.0
        val maxPrice = prices.maxOrNull() ?: 0.0
        val avgPrice = prices.average()

        // тут добавляешь расчёты по дням/неделям/месяцам

        return Statistics(minPrice, maxPrice, avgPrice /* avgPerDay, avgPerWeek, avgPerMonth */)
    }

    fun addReceipt(receipt: ReceiptEntity) {
        viewModelScope.launch {
            repository.insertReceipt(receipt)
        }
    }
}
