package com.example.qrmealtrack.presentation.trends.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrmealtrack.domain.usecase.GetFilteredChartPointsUseCase
import com.example.qrmealtrack.presentation.components.FilterType
import com.example.qrmealtrack.presentation.components.getDefaultCategories
import com.example.qrmealtrack.presentation.trends.components.GranularityType
import com.example.qrmealtrack.presentation.trends.model.mapper.ChartPointUiMapper
import com.example.qrmealtrack.presentation.trends.state.TrendsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TrendsViewModel @Inject constructor(
    private val getFilteredPointsUseCase: GetFilteredChartPointsUseCase,
    private val chartPointUiMapper: ChartPointUiMapper
) : ViewModel() {

    private val _granularity = MutableStateFlow(GranularityType.WEEK)
    private val _filter = MutableStateFlow(getDefaultCategories())
    private val _useLogScale = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TrendsUiState> = combine(
        _granularity,
        _filter,
        _useLogScale
    ) { granularity, filter, useLogScale ->
        Triple(granularity, filter, useLogScale)
    }.flatMapLatest { (granularity, filter, useLogScale) ->
        val selectedKeys = filter.getSelectedKeys()

        getFilteredPointsUseCase(granularity, selectedKeys)
            .map { domainPoints ->
                val uiPoints = chartPointUiMapper.map(domainPoints, granularity)

                TrendsUiState(
                    filter = filter,
                    granularity = granularity,
                    groupedPoints = uiPoints,
                    useLogScale = useLogScale
                )
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TrendsUiState(
            filter = getDefaultCategories(),
            granularity = GranularityType.WEEK,
            groupedPoints = emptyMap(),
            useLogScale = false
        )
    )



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

    fun toggleLogScale() {
        _useLogScale.update { !it }
    }
}

