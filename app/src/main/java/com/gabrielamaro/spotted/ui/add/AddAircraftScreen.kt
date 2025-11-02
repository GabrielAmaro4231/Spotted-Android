package com.gabrielamaro.spotted.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gabrielamaro.spotted.data.defaultManufacturers
import com.gabrielamaro.spotted.data.defaultAirports
import com.gabrielamaro.spotted.ui.home.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAircraftScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    editTail: String? = null // optional parameter for editing
) {
    val items by viewModel.aircrafts.collectAsState()
    val isEditMode = editTail != null

    // find existing aircraft
    val existingAircraft = remember(editTail, items) {
        editTail?.let { tail ->
            items.firstOrNull { it.tail.equals(tail, ignoreCase = true) }
        }
    }

    var selectedManufacturer by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf("") }
    var selectedAirport by remember { mutableStateOf("") }
    var registration by remember { mutableStateOf("") }

    // preload existing data if editing
    LaunchedEffect(existingAircraft) {
        existingAircraft?.let { a ->
            selectedManufacturer = a.manufacturer
            selectedModel = a.model
            selectedAirport = "${a.airportCity} (${a.airportIcao} - ${a.airportIata})"
            registration = a.tail
        }
    }

    val availableModels =
        defaultManufacturers.find { it.name == selectedManufacturer }?.models ?: emptyList()

    val currentDate = remember {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        formatter.format(Date())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Aircraft" else "Add New Aircraft") },
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
                modifier = Modifier.fillMaxWidth(),
                enabled = !isEditMode
            )

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

                        viewModel.addOrUpdateAircraft(
                            id = existingAircraft?.id, // preserve ID for editing
                            tail = registration,
                            manufacturer = selectedManufacturer,
                            model = selectedModel,
                            airportCity = city,
                            airportIcao = icao,
                            airportIata = iata,
                            datetime = existingAircraft?.datetime ?: currentDate
                        )

                        // ✅ Always return to Home screen after saving
                        navController.popBackStack("home", inclusive = false)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedManufacturer.isNotEmpty() &&
                        selectedModel.isNotEmpty() &&
                        selectedAirport.isNotEmpty() &&
                        registration.isNotEmpty()
            ) {
                Text(if (isEditMode) "Save Changes" else "Add Aircraft")
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
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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
