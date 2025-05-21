package com.example.qrmealtrack.presentation.main

import androidx.compose.runtime.Immutable

@Immutable
data class MainUiState(
    val currentTab:BottomTab = BottomTab.HOME
)