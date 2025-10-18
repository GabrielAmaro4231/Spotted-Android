package com.gabrielamaro.spotted.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gabrielamaro.spotted.ui.home.HomeScreen
import com.gabrielamaro.spotted.ui.login.LoginScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginClick = { navController.navigate("home") })
        }
        composable("home") {
            HomeScreen(navController)
        }
    }
}
