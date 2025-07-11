package com.example.qrmealtrack.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.domain.usecase.GetAllReceiptsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections.emptyList
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllReceiptsUseCase: GetAllReceiptsUseCase
) : ViewModel(){
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _receipts = MutableStateFlow<List<Receipt>>(emptyList())

    init {
        viewModelScope.launch {
            getAllReceiptsUseCase().collect { receiptList ->
                _receipts.value = receiptList
            }
        }
    }


    fun onTabSelected(tab: BottomTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }


    fun goToProfileAfterScan() {
        _uiState.update { it.copy(currentTab = BottomTab.STATS) }
    }
}