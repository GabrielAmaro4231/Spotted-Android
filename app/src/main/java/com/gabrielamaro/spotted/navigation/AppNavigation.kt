package com.gabrielamaro.spotted.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gabrielamaro.spotted.ui.home.HomeScreen
import com.gabrielamaro.spotted.ui.details.AircraftDetails
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
        composable(
            route = "details/{tail}/{manufacturer}/{model}/{airportCity}/{airportIcao}/{airportIata}/{datetime}",
            arguments = listOf(
                navArgument("tail") { type = NavType.StringType },
                navArgument("manufacturer") { type = NavType.StringType },
                navArgument("model") { type = NavType.StringType },
                navArgument("airportCity") { type = NavType.StringType },
                navArgument("airportIcao") { type = NavType.StringType },
                navArgument("airportIata") { type = NavType.StringType },
                navArgument("datetime") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments!!
            val tail = args.getString("tail") ?: ""
            val manufacturer = args.getString("manufacturer") ?: ""
            val model = args.getString("model") ?: ""
            val airportCity = args.getString("airportCity") ?: ""
            val airportIcao = args.getString("airportIcao") ?: ""
            val airportIata = args.getString("airportIata") ?: ""
            val datetime = args.getString("datetime") ?: ""

            AircraftDetails(
                navController = navController,
                tail = tail,
                manufacturer = manufacturer,
                model = model,
                airportCity = airportCity,
                airportIcao = airportIcao,
                airportIata = airportIata,
                datetime = datetime
            )
        }
    }
}
