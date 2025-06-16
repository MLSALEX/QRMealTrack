package com.example.qrmealtrack.presentation.stats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrmealtrack.domain.model.PriceChangeItem
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import com.example.qrmealtrack.domain.usecase.GetFilteredStatsUseCase
import com.example.qrmealtrack.domain.usecase.GetPriceDynamicsUseCase
import com.example.qrmealtrack.domain.usecase.GetStatisticsUseCase
import com.example.qrmealtrack.domain.usecase.StatsSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getStatisticsUseCase: GetStatisticsUseCase,
    private val getPriceDynamicsUseCase: GetPriceDynamicsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        observeSummary()
        observePriceChanges()
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

    fun onFilterSelected(filter: TimeFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }
}