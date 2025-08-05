package com.example.qrmealtrack.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.qrmealtrack.presentation.main.BottomNavigationBar
import com.example.qrmealtrack.presentation.main.BottomTab

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    showBottomBar: Boolean = false,
    currentTab: BottomTab = BottomTab.HOME,
    onTabSelected: (BottomTab) -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentTab = currentTab,
                    onTabSelected = onTabSelected
                )
            }
        },
        content = content
    )
}

@Composable
fun Modifier.withScaffoldPadding(padding: PaddingValues): Modifier = this
    .padding(padding)
    .consumeWindowInsets(padding)
    .consumeWindowInsets(WindowInsets.systemBars)