package com.gabrielamaro.spotted.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gabrielamaro.spotted.ui.add.AddAircraftScreen
import com.gabrielamaro.spotted.ui.home.HomeScreen
import com.gabrielamaro.spotted.ui.home.HomeViewModel
import com.gabrielamaro.spotted.ui.login.LoginScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(navController: NavHostController) {

    // Shared ViewModel across Home + AddAircraft
    val homeViewModel: HomeViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // ----------------------
        // LOGIN
        // ----------------------
        composable("login") {
            LoginScreen(
                onLoginClick = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // ----------------------
        // HOME SCREEN
        // ----------------------
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = homeViewModel
            )
        }

        // ----------------------
        // ADD NEW AIRCRAFT
        // ----------------------
        composable("addAircraft/new") {

            // NEW MODE → reset the selected post
            homeViewModel.updateSelectedPost(null)

            AddAircraftScreen(
                navController = navController,
                viewModel = homeViewModel
            )
        }

        // ----------------------
        // VIEW / EDIT AIRCRAFT
        // ----------------------
        composable("addAircraft/view") {

            // VIEW MODE → HomeScreen already set selectedPost before navigation
            AddAircraftScreen(
                navController = navController,
                viewModel = homeViewModel
            )
        }
    }
}
