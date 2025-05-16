package com.example.qrmealtrack.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.qrmealtrack.presentation.home.HomeScreen
import com.example.qrmealtrack.presentation.navigation.BottomTab
import com.example.qrmealtrack.presentation.profile.ProfileScreen
import com.example.qrmealtrack.presentation.scan.ScanScreen

@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val receipts by viewModel.receipts.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentTab = state.currentTab,
                onTabSelected = { viewModel.onEvent(MainUiEvent.TabSelected(it)) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (state.currentTab) {
                BottomTab.HOME -> HomeScreen()
                BottomTab.SCAN -> ScanScreen(
                    navController = navController,
                    onScanned = {
                        viewModel.onEvent(MainUiEvent.TabSelected(BottomTab.HOME))
                    }
                )
                BottomTab.PROFILE -> ProfileScreen()
            }
        }
    }
}
