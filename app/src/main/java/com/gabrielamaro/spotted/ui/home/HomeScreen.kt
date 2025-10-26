package com.gabrielamaro.spotted.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gabrielamaro.spotted.data.local.entity.AircraftEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // Observe Room Flow as Compose state
    val items by viewModel.aircrafts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spotted ✈️") },
                actions = {
                    TextButton(onClick = { navController.navigate("login") }) {
                        Text("Quit")
                    }
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
        if (items.isEmpty()) {
            // Display empty state message when no data is found
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nothing spotted yet.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            // Display list of aircrafts
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
}
