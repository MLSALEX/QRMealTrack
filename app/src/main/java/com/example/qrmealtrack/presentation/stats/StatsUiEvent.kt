package com.example.qrmealtrack.presentation.stats

sealed interface StatsUiEvent {
    data object NavigateToTrends : StatsUiEvent
}