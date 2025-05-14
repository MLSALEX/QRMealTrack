package com.example.qrmealtrack.presentation.main

import com.example.qrmealtrack.presentation.navigation.BottomTab

sealed interface MainUiEvent {
    data class TabSelected(val tab: BottomTab): MainUiEvent
}