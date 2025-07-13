package com.example.qrmealtrack.ui.utils

import android.app.Activity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

fun Activity.enableEdgeToEdge(
    navigationBarColor: Int,
    isDarkIcons: Boolean = false
) {
    WindowCompat.setDecorFitsSystemWindows(window, false)

    window.navigationBarColor = navigationBarColor
    window.statusBarColor = navigationBarColor

    WindowInsetsControllerCompat(window, window.decorView).apply {
        isAppearanceLightNavigationBars = isDarkIcons
        isAppearanceLightStatusBars = isDarkIcons
    }
}