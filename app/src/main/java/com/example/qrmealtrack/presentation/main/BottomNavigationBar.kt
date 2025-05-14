package com.example.qrmealtrack.presentation.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.qrmealtrack.R
import com.example.qrmealtrack.presentation.navigation.BottomTab

data class BottomNavItem(
    val tab: BottomTab,
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int
)

private val bottomNavItems = listOf(
    BottomNavItem(BottomTab.HOME, R.drawable.home_icon, R.string.home),
    BottomNavItem(BottomTab.SCAN, R.drawable.scan_icon, R.string.scan),
    BottomNavItem(BottomTab.PROFILE, R.drawable.profile_icon, R.string.profile)
)
@Composable
fun BottomNavigationBar (
    currentTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    NavigationBar {
        bottomNavItems.forEach{ item ->
            NavigationBarItem(
                selected = currentTab == item.tab,
                onClick = { onTabSelected(item.tab)},
                icon = {
                    Icon(painterResource(item.iconRes), contentDescription = null)
                },
                label = { Text(stringResource(item.labelRes)) }
            )

        }
    }
}