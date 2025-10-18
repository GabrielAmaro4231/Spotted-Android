package com.gabrielamaro.spotted.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    // Placeholder data: tail number and model
    val items = List(38) { index ->
        AircraftPlaceholder(
            tail = "PT-${1000 + index}",
            model = when (index % 5) {
                0 -> "Boeing 737"
                1 -> "Airbus A320"
                2 -> "Embraer E190"
                3 -> "Cessna 172"
                else -> "Boeing 777"
            }
        )
    }

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
                    model = item.model,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}

// Simple holder used only in this file for placeholders
data class AircraftPlaceholder(val tail: String, val model: String)
