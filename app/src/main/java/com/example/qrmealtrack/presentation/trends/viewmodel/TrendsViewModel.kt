package com.example.qrmealtrack.presentation.trends.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrmealtrack.domain.usecase.GetFilteredChartPointsUseCase
import com.example.qrmealtrack.presentation.components.FilterType
import com.example.qrmealtrack.presentation.components.getDefaultCategories
import com.example.qrmealtrack.presentation.trends.components.GranularityType
import com.example.qrmealtrack.presentation.trends.model.UiChartPoint
import com.example.qrmealtrack.presentation.trends.state.TrendsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TrendsViewModel @Inject constructor(
    private val getFilteredPointsUseCase: GetFilteredChartPointsUseCase
) : ViewModel() {

    private val _granularity = MutableStateFlow(GranularityType.WEEK)
    private val _filter = MutableStateFlow(getDefaultCategories())


    val uiState: StateFlow<TrendsUiState> = combine(_granularity, _filter) { granularity, filter ->
        val selectedKeys = filter.getSelectedKeys()
        val points = getFilteredPointsUseCase(granularity, selectedKeys)
            .groupBy { it.category }

        TrendsUiState(
            filter = filter,
            granularity = granularity,
            groupedPoints = points
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TrendsUiState(
        filter = getDefaultCategories(),
        granularity = GranularityType.WEEK,
        groupedPoints = emptyMap()
    ))

    fun onFilterChange(newFilter: FilterType.Categories) {
        _filter.value = newFilter
    }

    fun onGranularityChange(newGranularity: GranularityType) {
        _granularity.value = newGranularity
    }

    fun clearFilter() {
        _filter.update { old ->
            FilterType.Categories(old.categories.map { it.copy(isSelected = false) }.toSet())
        }
    }
}
