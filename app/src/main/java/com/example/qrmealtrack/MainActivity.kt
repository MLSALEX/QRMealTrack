package com.example.qrmealtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.qrmealtrack.navigation.NavGraph
import com.example.qrmealtrack.presentation.trends.TrendsScreen
import com.example.qrmealtrack.ui.theme.QRMealTrackTheme
import com.example.qrmealtrack.ui.utils.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var isLoading by mutableStateOf(true)
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { isLoading }

        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            navigationBarColor = ContextCompat.getColor(this, R.color.background),
            isDarkIcons = false
        )
        setContent {
            QRMealTrackTheme {
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    delay(500)
                    isLoading = false
                }
                NavGraph(navController)

//                TrendsScreen()
            }
        }
    }
}
