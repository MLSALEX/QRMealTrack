package com.example.qrmealtrack.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.qrmealtrack.presentation.ReceiptFromWebScreen
import com.example.qrmealtrack.presentation.main.MainScreen
import com.example.qrmealtrack.presentation.main.MainViewModel
import com.example.qrmealtrack.presentation.permission.PermissionScreen
import com.example.qrmealtrack.presentation.scan.ScanScreen
import java.net.URLDecoder

sealed class Screen(val route: String) {
    data object Permission : Screen("permission")
    data object Main : Screen("main")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Permission.route) {
        composable("permission") {
            PermissionScreen(
                permission = android.Manifest.permission.CAMERA,
                onPermissionGranted = {  navController.navigate(Screen.Main.route) },
                onPermissionDenied = { /* Показать диалог / навигация на error экран */ }
            )
        }
        composable(Screen.Main.route) {
            MainScreen(parentNavController = navController)
        }

        composable(
            route = "receipt_from_web/{url}",
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("url") ?: ""
            val decodedUrl = URLDecoder.decode(encodedUrl, "UTF-8")
            val mainViewModel: MainViewModel = hiltViewModel()
            ReceiptFromWebScreen(
                url = decodedUrl,
                onDone = {
                    mainViewModel.goToProfileAfterScan()
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}