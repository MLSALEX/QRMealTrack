package com.example.qrmealtrack.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrmealtrack.data.local.ReceiptEntity
import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import com.example.qrmealtrack.domain.usecase.FetchWebPageInfoUseCase
import com.example.qrmealtrack.domain.usecase.GetReceiptsGroupedByDayUseCase
import com.example.qrmealtrack.domain.usecase.SaveParsedReceiptUseCase
import com.example.qrmealtrack.presentation.utils.ParsedReceipt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceiptListViewModel @Inject constructor(
    private val repository: ReceiptRepository,
    private val fetchWebPageInfoUseCase: FetchWebPageInfoUseCase,
    private val saveParsedReceiptUseCase: SaveParsedReceiptUseCase,
    private val getReceiptsGroupedByDayUseCase: GetReceiptsGroupedByDayUseCase
) :
    ViewModel() {
    private val _state = MutableStateFlow(ReceiptUiState())
    val state = _state.asStateFlow()

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    fun saveParsedReceipt(parsed: ParsedReceipt) {
        viewModelScope.launch {
            val result = saveParsedReceiptUseCase(parsed)
            if (result.isSuccess) {
                _message.emit("Чек добавлен")
            } else {
                _message.emit(result.exceptionOrNull()?.message ?: "Ошибка")
            }
        }
    }

    init {
        viewModelScope.launch {
            getReceiptsGroupedByDayUseCase().collect { result ->
                Log.d("💡VM", "Всего групп: ${result.receiptsByDay.size}")
                result.receiptsByDay.forEach { (date, list) ->
                    Log.d("📅", "$date → ${list.size} чеков")
                }

                _state.update {
                    it.copy(
                        receiptsByDay = result.receiptsByDay,
                        totalsByDay = result.totalsByDay,
                        // если хочешь посчитать stats:
                        statistics = calculateStats(result.receiptsByDay.values.flatten())
                    )
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

    fun fetchWebPageInfo(url: String) {
        viewModelScope.launch {
            val result = fetchWebPageInfoUseCase(url)
            _state.update { it.copy(webPageInfo = result) }
        }
    }
}
