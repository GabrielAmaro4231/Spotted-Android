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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URL

// ✔️ IMPORT MODELS FROM YOUR MODELS.KT
import com.gabrielamaro.spotted.model.Airport
import com.gabrielamaro.spotted.model.PostInsert

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAircraftScreen(navController: NavController, viewModel: HomeViewModel) {

    var prefix by remember { mutableStateOf("") }
    var prefixError by remember { mutableStateOf<String?>(null) }

    var airportResults by remember { mutableStateOf<List<Airport>>(emptyList()) }
    var airportMenuExpanded by remember { mutableStateOf(false) }
    var selectedAirport by remember { mutableStateOf<Airport?>(null) }
    var airportSearchText by remember { mutableStateOf("") }
    var searchJob by remember { mutableStateOf<Job?>(null) }

    var loading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ---------------------------------------------------------
    // SERVER-SIDE SEARCH (correct version using filter builder)
    // ---------------------------------------------------------
    fun performAirportSearch(query: String) {
        searchJob?.cancel()
        searchJob = scope.launch {
            delay(250)

            val q = query.trim()
            if (q.length < 2) {
                airportResults = emptyList()
                return@launch
            }

            try {
                val response = supabase.from("airport_list").select {
                    filter {
                        or {
                            ilike("airport_name", "%$q%")
                            ilike("airport_icao", "%$q%")
                            ilike("airport_iata", "%$q%")
                            ilike("airport_city", "%$q%")
                        }
                    }
                    limit(20)
                }

                airportResults = response.decodeList<Airport>()

            } catch (e: Exception) {
                airportResults = emptyList()
            }
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

            // ---------------------------------------------------------
            // Aircraft Prefix
            // ---------------------------------------------------------
            Column {
                OutlinedTextField(
                    value = prefix,
                    onValueChange = {
                        prefix = it.uppercase()
                        prefixError = null
                    },
                    label = { Text("Aircraft Prefix (e.g. PR-ABC)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = prefixError != null
                )

                if (prefixError != null) {
                    Text(
                        text = prefixError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            // ---------------------------------------------------------
            // AIRPORT DROPDOWN SEARCH
            // ---------------------------------------------------------
            ExposedDropdownMenuBox(
                expanded = airportMenuExpanded,
                onExpandedChange = { airportMenuExpanded = it }
            ) {

                OutlinedTextField(
                    value = airportSearchText,
                    onValueChange = { text ->
                        airportSearchText = text
                        airportMenuExpanded = true
                        selectedAirport = null
                        performAirportSearch(text)
                    },
                    label = { Text("Select Airport") },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = airportMenuExpanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = airportMenuExpanded,
                    onDismissRequest = { airportMenuExpanded = false }
                ) {

                    if (airportResults.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No matching airports") },
                            onClick = {}
                        )
                    } else {
                        airportResults.forEach { airport ->
                            DropdownMenuItem(
                                text = {
                                    Text("${airport.airport_name} (${airport.airport_icao})")
                                },
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

            // ---------------------------------------------------------
            // Placeholder image picker
            // ---------------------------------------------------------
            OutlinedTextField(
                value = "Image picker coming soon...",
                onValueChange = {},
                enabled = false,
                label = { Text("Photo") },
                modifier = Modifier.fillMaxWidth()
            )

            // ---------------------------------------------------------
            // Add Button (with API + DB insert)
            // ---------------------------------------------------------
            Button(
                onClick = {
                    val pfx = prefix.trim()
                    val airportId = selectedAirport?.id ?: return@Button

                    loading = true
                    prefixError = null

                    scope.launch {
                        try {

                            val url = "https://www.jetapi.dev/api?reg=$pfx&photos=0&flights=0"

                            val jsonText = withContext(kotlinx.coroutines.Dispatchers.IO) {
                                URL(url).readText()
                            }

                            val json = Json.parseToJsonElement(jsonText).jsonObject

                            val flightRadarElement = json["FlightRadar"]
                            if (flightRadarElement == null || flightRadarElement.toString() == "null") {
                                prefixError = "No aircraft found with this prefix."
                                loading = false
                                return@launch
                            }

                            val model =
                                flightRadarElement.jsonObject["Aircraft"]?.jsonPrimitive?.content

                            if (model.isNullOrBlank()) {
                                prefixError = "No aircraft model information available."
                                loading = false
                                return@launch
                            }

                            // INSERT INTO posts
                            supabase.from("posts").insert(
                                PostInsert(
                                    aircraft_prefix = pfx,
                                    aircraft_model = model,
                                    airport_id = airportId,
                                    content = ""
                                )
                            )

                            Toast.makeText(context, "Aircraft added!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()

                        } catch (e: Exception) {
                            prefixError = "Insert failed: ${e.message}"
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = prefix.isNotBlank() && selectedAirport != null && !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (loading) "Adding..." else "Add Aircraft")
            }
        }
    }
}
