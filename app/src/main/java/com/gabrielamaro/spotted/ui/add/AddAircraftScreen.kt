package com.gabrielamaro.spotted.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gabrielamaro.spotted.data.defaultManufacturers
import com.gabrielamaro.spotted.data.defaultAirports
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.gabrielamaro.spotted.ui.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAircraftScreen(navController: NavController, viewModel: HomeViewModel) {
    var selectedManufacturer by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf("") }
    var selectedAirport by remember { mutableStateOf("") }
    var registration by remember { mutableStateOf("") }

    val availableModels = defaultManufacturers.find { it.name == selectedManufacturer }?.models ?: emptyList()

    val currentDate = remember {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        formatter.format(Date())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Aircraft") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DropdownMenuBox(
                items = defaultManufacturers.map { it.name },
                selectedItem = selectedManufacturer,
                onItemSelected = {
                    selectedManufacturer = it
                    selectedModel = ""
                },
                placeholder = "Select Manufacturer"
            )

            DropdownMenuBox(
                items = availableModels,
                selectedItem = selectedModel,
                onItemSelected = { selectedModel = it },
                placeholder = "Select Model",
                enabled = selectedManufacturer.isNotEmpty()
            )

            DropdownMenuBox(
                items = defaultAirports.map { "${it.city} (${it.icao} - ${it.iata})" },
                selectedItem = selectedAirport,
                onItemSelected = { selectedAirport = it },
                placeholder = "Select Airport"
            )

            OutlinedTextField(
                value = registration,
                onValueChange = { registration = it },
                label = { Text("Aircraft Registration") },
                modifier = Modifier.fillMaxWidth()
            )

//            OutlinedTextField(
//                value = "Image picker coming soon...",
//                onValueChange = {},
//                label = { Text("Photo") },
//                readOnly = true,
//                modifier = Modifier.fillMaxWidth()
//            )

            Button(
                onClick = {
                    if (selectedManufacturer.isNotEmpty() &&
                        selectedModel.isNotEmpty() &&
                        selectedAirport.isNotEmpty() &&
                        registration.isNotEmpty()
                    ) {
                        val airportParts = selectedAirport
                            .substringAfter("(")
                            .substringBefore(")")
                            .split(" - ")

                        val icao = airportParts.getOrNull(0)?.trim() ?: ""
                        val iata = airportParts.getOrNull(1)?.trim() ?: ""
                        val city = selectedAirport.substringBefore(" (")

                        viewModel.addAircraft(
                            tail = registration,
                            manufacturer = selectedManufacturer,
                            model = selectedModel,
                            airportCity = city,
                            airportIcao = icao,
                            airportIata = iata,
                            datetime = currentDate
                        )

                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedManufacturer.isNotEmpty() &&
                        selectedModel.isNotEmpty() &&
                        selectedAirport.isNotEmpty() &&
                        registration.isNotEmpty()
            ) {
                Text("Add Aircraft")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuBox(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    placeholder: String,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            label = { Text(placeholder) },
            readOnly = true,
            enabled = enabled,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
