package com.gabrielamaro.spotted.ui.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gabrielamaro.spotted.data.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val items = viewModel.aircrafts

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spotted ✈️") },
                actions = {
                    GoogleSignOutButton(navController)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addAircraft") }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add aircraft"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            items(items) { item ->
                AircraftItem(
                    tail = item.tail,
                    manufacturer = item.manufacturer,
                    model = item.model,
                    airportCity = item.airportCity,
                    airportIcao = item.airportIcao,
                    airportIata = item.airportIata,
                    datetime = item.datetime,
                    navController = navController,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun GoogleSignOutButton(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val onClick: () -> Unit = {
        coroutineScope.launch {
            try {
                supabase.auth.signOut()
                Toast.makeText(context, "Signed out successfully!", Toast.LENGTH_SHORT).show()

                // Redirect to login screen and clear backstack
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }

            } catch (e: Exception) {
                Log.e("GoogleSignOut", "SignOut error", e)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Button(onClick = onClick) {
        Text("Quit")
    }
}

