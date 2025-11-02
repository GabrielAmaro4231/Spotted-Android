package com.gabrielamaro.spotted.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gabrielamaro.spotted.ui.details.AircraftDetails
import com.gabrielamaro.spotted.ui.home.HomeScreen
import com.gabrielamaro.spotted.ui.home.HomeViewModel
import com.gabrielamaro.spotted.ui.login.LoginScreen
import com.gabrielamaro.spotted.ui.add.AddAircraftScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    val homeViewModel: HomeViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(onLoginClick = { navController.navigate("home") })
        }

        composable("home") {
            HomeScreen(navController, viewModel = homeViewModel)
        }

        composable("addAircraft") {
            AddAircraftScreen(navController, homeViewModel)
        }

        composable(
            route = "addAircraft?editTail={editTail}",
            arguments = listOf(
                navArgument("editTail") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val editTail = backStackEntry.arguments?.getString("editTail")
            AddAircraftScreen(navController, homeViewModel, editTail)
        }

        composable(
            route = "details/{id}/{tail}/{manufacturer}/{model}/{airportCity}/{airportIcao}/{airportIata}/{datetime}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
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
            val id = args.getInt("id")
            AircraftDetails(
                navController = navController,
                id = id,
                tail = args.getString("tail") ?: "",
                manufacturer = args.getString("manufacturer") ?: "",
                model = args.getString("model") ?: "",
                airportCity = args.getString("airportCity") ?: "",
                airportIcao = args.getString("airportIcao") ?: "",
                airportIata = args.getString("airportIata") ?: "",
                datetime = args.getString("datetime") ?: "",
                viewModel = homeViewModel
            )
        }
    }
}
