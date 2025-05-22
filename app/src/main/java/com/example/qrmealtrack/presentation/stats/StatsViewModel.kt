package com.example.qrmealtrack.presentation.stats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import com.example.qrmealtrack.domain.usecase.GetFilteredStatsUseCase
import com.example.qrmealtrack.domain.usecase.StatsSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getFilteredStatsUseCase: GetFilteredStatsUseCase,
    private val repository: ReceiptRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(TimeFilter.Week)
    val selectedFilter: StateFlow<TimeFilter> = _selectedFilter.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val statsState: StateFlow<StatsSummary> = selectedFilter
        .flatMapLatest { filter -> getFilteredStatsUseCase(filter) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsSummary(0.0, 0.0, null, 0.0, 0))

    init {
        // ✅ Вставь сюда лог:
        viewModelScope.launch {
            repository.getAllReceipts().collect { list ->
                Log.d("📦DB", "Получено ${list.size} записей")
                Log.d("📦DB", "Суммарный вес: ${list.sumOf { it.weight }} г")
                Log.d("📦DB", "Примеры блюд: ${list.take(3).joinToString { "${it.itemName} (${it.weight} г)" }}")
            }
        }
    }

    fun onFilterSelected(filter: TimeFilter) {
        _selectedFilter.value = filter
    }
}