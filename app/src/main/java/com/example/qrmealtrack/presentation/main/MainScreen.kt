package com.example.qrmealtrack.presentation.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.qrmealtrack.R
import com.example.qrmealtrack.navigation.Screen
import com.example.qrmealtrack.presentation.home.HomeScreen
import com.example.qrmealtrack.presentation.stats.StatsScreen

import com.example.qrmealtrack.presentation.scan.ScanScreen

@Composable
fun MainScreen(
    parentNavController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val savedStateHandle = navBackStackEntry?.savedStateHandle

    // Listen for result from ReceiptFromWebScreen
    LaunchedEffect(savedStateHandle) {
        val navigateToProfile = savedStateHandle?.get<Boolean>("navigateToProfile") ?: false
        if (navigateToProfile) {
            viewModel.goToProfileAfterScan()
            savedStateHandle?.set("navigateToProfile", false) // reset
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentTab = state.currentTab,
                onTabSelected = { tab  ->
                    viewModel.onTabSelected(tab )
                    navController.navigate(tab .route) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        restoreState = true
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.navigationBars
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomTab.HOME.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomTab.HOME.route) {
                HomeScreen()
            }
            composable(BottomTab.SCAN.route) {
                ScanScreen(
                    navController = navController, // для перехода между табами
                    parentNavController = parentNavController, // для глобального перехода
                    onScanned = {
                        navController.navigate(Screen.Home.route) {
                            launchSingleTop = true
                            popUpTo(Screen.Home.route) { saveState = true }
                            restoreState = true
                        }
                    }
                )
            }
            composable(BottomTab.STATS.route) {
                StatsScreen()
            }
        }
    }
}


@Composable
fun BottomNavigationBar(
    currentTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    NavigationBar {
        BottomTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = tab == currentTab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        painter = painterResource(id = tab.icon),
                        contentDescription = null
                    )
                },
                label = { Text(text = stringResource(id = tab.label))}
            )
        }
    }
}

enum class BottomTab(
    private val screen: Screen, // Связываем с sealed class
    @StringRes val label: Int,
    @DrawableRes val icon: Int
) {
    HOME(Screen.Home, R.string.home, R.drawable.home_icon),
    SCAN(Screen.Scan, R.string.scan, R.drawable.scan_icon),
    STATS(Screen.Stats, R.string.stats, R.drawable.trending_up);

    val route: String
        get() = screen.route // Получаем маршрут напрямую
}
