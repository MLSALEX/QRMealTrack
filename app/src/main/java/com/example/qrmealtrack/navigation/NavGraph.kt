package com.example.qrmealtrack.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.qrmealtrack.presentation.receipt.ReceiptFromWebScreen
import com.example.qrmealtrack.presentation.main.MainScreen
import com.example.qrmealtrack.presentation.main.MainViewModel
import com.example.qrmealtrack.presentation.permission.PermissionScreen
import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screen(
    val route: String
) {
    data object Permission : Screen("permission")
    data object Main : Screen("main")
    data object Home : Screen("home")
    data object Scan : Screen("scan")
    data object Stats : Screen("stats")
    data class ReceiptFromWeb(val url: String) : Screen("receipt_from_web/{url}") {
        fun createRoute(url: String) = "receipt_from_web/${URLEncoder.encode(url, "UTF-8")}"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Permission.route) {
        composable(Screen.Permission.route) {
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
            route =  Screen.ReceiptFromWeb("{url}").route,
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