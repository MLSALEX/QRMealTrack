package com.example.qrmealtrack.presentation.stats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import com.example.qrmealtrack.domain.usecase.GetCategoryStatsUseCase
import com.example.qrmealtrack.domain.usecase.GetPriceDynamicsUseCase
import com.example.qrmealtrack.domain.usecase.GetStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getStatisticsUseCase: GetStatisticsUseCase,
    private val getPriceDynamicsUseCase: GetPriceDynamicsUseCase,
    private val getCategoryStatsUseCase: GetCategoryStatsUseCase,
    private val repository: ReceiptRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        observeSummary()
        observePriceChanges()
        observeCategoryStats()

        debugRawReceipts()
    }

    private fun debugRawReceipts() {
        viewModelScope.launch {
            repository.getAllReceipts().collect { receipts ->
                Log.d("DEBUG_RAW", "=== RECEIPTS COUNT=${receipts.size} ===")
                receipts.forEach { receipt ->
                    Log.d("DEBUG_RAW", "Receipt id=${receipt.id} category=${receipt.category.key} total=${receipt.total}")
                    receipt.items.forEach { item ->
                        Log.d(
                            "DEBUG_RAW",
                            "  ITEM name=${item.name} category=${item.category?.key ?: "null"} price=${item.price}"
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSummary() {
        viewModelScope.launch {
            _uiState
                .map { it.selectedFilter }
                .distinctUntilChanged()
                .flatMapLatest { getStatisticsUseCase(it) }
                .collect { stats ->
                    _uiState.update { it.copy(summary = stats) }
                }
        }
    }

    private fun observePriceChanges() {
        viewModelScope.launch {
            getPriceDynamicsUseCase().collect { changes ->
                _uiState.update { it.copy(priceDynamics = changes) }
            }
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeCategoryStats() {
        viewModelScope.launch {
            _uiState
                .map { it.selectedFilter }
                .distinctUntilChanged()
                .flatMapLatest { filter -> getCategoryStatsUseCase(filter) }
                .collect { stats ->

                    Log.d("DEBUG_STATS", "Category stats count = ${stats.size}")
                    stats.forEach {
                        Log.d("DEBUG_STATS", "Category=${it.category}, total=${it.total}")
                    }

                    _uiState.update { it.copy(categoryStats = stats) }
                }
        }
    }

    fun onFilterSelected(filter: TimeFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }
}