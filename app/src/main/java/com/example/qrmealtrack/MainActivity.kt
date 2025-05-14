package com.example.qrmealtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.qrmealtrack.navigation.NavGraph
import com.example.qrmealtrack.ui.theme.QRMealTrackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QRMealTrackTheme {
                val navController = rememberNavController()
                NavGraph(navController)
            }
        }
    }
}
