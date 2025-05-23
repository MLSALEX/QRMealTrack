package com.example.qrmealtrack.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrmealtrack.data.mapper.toDomain
import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ReceiptRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllReceipts()
                .map { list ->
                    list
                        .map { it.toDomain() } // преобразование в Receipt
                        .groupBy { receipt ->
                            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                                .format(Date(receipt.dateTime))
                        }
                }
                .collect { grouped ->
                    _state.value = _state.value.copy(receiptsByDay = grouped)
                }
        }
    }
    fun deleteReceipt(receipt: Receipt) {
        viewModelScope.launch {
            repository.deleteReceiptGroup(receipt.fiscalCode, receipt.dateTime)
        }
    }
}