package com.example.qrmealtrack.presentation.stats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import com.example.qrmealtrack.domain.usecase.GetCategoryStatsUseCase
import com.example.qrmealtrack.domain.usecase.GetPriceDynamicsUseCase
import com.example.qrmealtrack.domain.usecase.GetStatisticsUseCase
import com.example.qrmealtrack.presentation.stats.colors_provider.defaultCategoryColors
import com.example.qrmealtrack.presentation.stats.components.LabelMode
import com.example.qrmealtrack.presentation.stats.model.CategoryUiModel
import com.example.qrmealtrack.presentation.stats.model.toUiModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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

    private val _labelMode = MutableStateFlow(LabelMode.PERCENT)
    val labelMode: StateFlow<LabelMode> = _labelMode.asStateFlow()

    init {
        observeSummary()
        observePriceChanges()
        observeCategoryStats()
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
            combine(
                _uiState.map { it.selectedFilter }
                    .distinctUntilChanged()
                    .flatMapLatest { getCategoryStatsUseCase(it) },
                _labelMode
            ) { stats, mode ->
                val colors = defaultCategoryColors()
                stats.toUiModels(colors, mode)
            }
                .distinctUntilChanged() // если результат не изменился – не обновляем UI
                .catch { e ->
                    Log.e("observeCategoryStats", "Failed to load stats", e)
                    emit(emptyList<CategoryUiModel>() to 0)
                }
                .collect { ( uiModels, total) ->
                _uiState.update {
                    it.copy(
                        categoryUiModels = uiModels,
                        totalCategoryValue = total
                    )
                }
            }
        }
    }

    fun toggleLabelMode() {
        _labelMode.update {
            when (it) {
                LabelMode.PERCENT -> LabelMode.NAME
                LabelMode.NAME -> LabelMode.BOTH
                LabelMode.BOTH -> LabelMode.PERCENT
            }
        }
    }

    fun onFilterSelected(filter: TimeFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }
}