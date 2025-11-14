package com.gabrielamaro.spotted.ui.add

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gabrielamaro.spotted.data.supabase
import com.gabrielamaro.spotted.ui.home.HomeViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

@Serializable
data class Airport(
    val id: Long? = null,
    val created_at: String? = null,
    val airport_name: String,
    val airport_icao: String,
    val airport_iata: String? = null,
    val airport_city: String? = null
)

@Serializable
data class PostInsert(
    val aircraft_prefix: String,
    val airport_id: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAircraftScreen(navController: NavController, viewModel: HomeViewModel) {

    var prefix by remember { mutableStateOf("") }

    // Airport selector state
    var airportList by remember { mutableStateOf<List<Airport>>(emptyList()) }
    var airportMenuExpanded by remember { mutableStateOf(false) }
    var selectedAirport by remember { mutableStateOf<Airport?>(null) }
    var airportSearchText by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Fetch airports
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = supabase.from("airport_list").select()
                airportList = response.decodeList<Airport>()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to fetch airports: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Filtering logic
    val filteredAirports = remember(airportSearchText, airportList) {
        val q = airportSearchText.lowercase().trim()
        if (q.isBlank()) airportList
        else airportList.filter { airport ->
            airport.airport_name.lowercase().contains(q) ||
                    (airport.airport_city?.lowercase()?.contains(q) == true) ||
                    airport.airport_icao.lowercase().contains(q) ||
                    (airport.airport_iata?.lowercase()?.contains(q) == true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Aircraft") },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            OutlinedTextField(
                value = prefix,
                onValueChange = { prefix = it.uppercase() },
                label = { Text("Aircraft Prefix (e.g. PR-ABC)") },
                modifier = Modifier.fillMaxWidth()
            )

            // ===============================
            // Smooth Searchable Airport Selector
            // ===============================
            ExposedDropdownMenuBox(
                expanded = airportMenuExpanded,
                onExpandedChange = { airportMenuExpanded = it }
            ) {

                OutlinedTextField(
                    value = airportSearchText,
                    onValueChange = { airportSearchText = it },
                    label = { Text("Select Airport") },
                    modifier = Modifier
                        .menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        )
                        .fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = airportMenuExpanded)
                    },
                    readOnly = false
                )

                ExposedDropdownMenu(
                    expanded = airportMenuExpanded,
                    onDismissRequest = { airportMenuExpanded = false }
                ) {

                    if (filteredAirports.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No matching airports") },
                            onClick = {}
                        )
                    } else {
                        filteredAirports.forEach { airport ->
                            DropdownMenuItem(
                                text = { Text("${airport.airport_name} (${airport.airport_icao})") },
                                onClick = {
                                    selectedAirport = airport
                                    airportSearchText =
                                        "${airport.airport_name} (${airport.airport_icao})"
                                    airportMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Disabled photo picker placeholder
            OutlinedTextField(
                value = "Image picker coming soon...",
                onValueChange = {},
                enabled = false,
                label = { Text("Photo") },
                modifier = Modifier.fillMaxWidth()
            )

            // ===============================
            // Insert into posts table
            // ===============================
            Button(
                onClick = {
                    val aircraftPrefix = prefix.trim()
                    val airportId = selectedAirport?.id ?: return@Button

                    scope.launch {
                        try {
                            supabase.from("posts").insert(
                                PostInsert(
                                    aircraft_prefix = aircraftPrefix,
                                    airport_id = airportId
                                )
                            )

                            Toast.makeText(context, "Aircraft added!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()

                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                enabled = prefix.isNotBlank() && selectedAirport != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Aircraft")
            }
        }
    }
}
