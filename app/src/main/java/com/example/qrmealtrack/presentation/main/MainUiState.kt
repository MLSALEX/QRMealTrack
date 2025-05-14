package com.example.qrmealtrack.presentation.main

import androidx.compose.runtime.Immutable
import com.example.qrmealtrack.presentation.navigation.BottomTab

@Immutable
data class MainUiState(
    val currentTab:BottomTab = BottomTab.HOME
)