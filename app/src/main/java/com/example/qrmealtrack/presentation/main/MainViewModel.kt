package com.example.qrmealtrack.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.domain.usecase.GetAllReceiptsUseCase
import com.example.qrmealtrack.presentation.navigation.BottomTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllReceiptsUseCase: GetAllReceiptsUseCase
) : ViewModel(){
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _receipts = MutableStateFlow<List<Receipt>>(emptyList())
    val receipts: StateFlow<List<Receipt>> = _receipts.asStateFlow()

    init {
        viewModelScope.launch {
            getAllReceiptsUseCase().collect { receiptList ->
                _receipts.value = receiptList
            }
        }
    }

    fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.TabSelected -> {
                _uiState.update { it.copy(currentTab = event.tab) }
            }
        }
    }

    fun goToProfileAfterScan() {
        _uiState.update { it.copy(currentTab = BottomTab.PROFILE) }
    }
}