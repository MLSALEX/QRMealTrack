package com.example.qrmealtrack.presentation.home

import androidx.lifecycle.ViewModel
import com.example.qrmealtrack.presentation.components.FilterType
import com.example.qrmealtrack.presentation.components.getDefaultCategories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeFilterViewModel @Inject constructor() : ViewModel() {

    private val _filterState = MutableStateFlow(getDefaultCategories())
    val filterState: StateFlow<FilterType.Categories> = _filterState

    fun updateFilter(newFilter: FilterType.Categories) {
        _filterState.value = newFilter
    }

    fun clearFilter() {
        _filterState.value = FilterType.Categories(
            _filterState.value.categories.map { it.copy(isSelected = false) }.toSet()
        )
    }

    fun getSelectedNames(): List<String> = _filterState.value.getSelectedNames()
}