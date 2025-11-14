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
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URL

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
    val aircraft_model: String,
    val airport_id: Long,
    val content: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAircraftScreen(navController: NavController, viewModel: HomeViewModel) {

    var prefix by remember { mutableStateOf("") }
    var prefixError by remember { mutableStateOf<String?>(null) }

    var airportList by remember { mutableStateOf<List<Airport>>(emptyList()) }
    var airportMenuExpanded by remember { mutableStateOf(false) }
    var selectedAirport by remember { mutableStateOf<Airport?>(null) }
    var airportSearchText by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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

            OutlinedTextField(
                value = "Image picker coming soon...",
                onValueChange = {},
                enabled = false,
                label = { Text("Photo") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val aircraftPrefix = prefix.trim()
                    val airportId = selectedAirport?.id ?: return@Button

                    loading = true
                    prefixError = null

                    scope.launch {
                        try {
                            val url =
                                "https://www.jetapi.dev/api?reg=$aircraftPrefix&photos=0&flights=0"

                            val jsonText = withContext(kotlinx.coroutines.Dispatchers.IO) {
                                URL(url).readText()
                            }

                            val json = Json.parseToJsonElement(jsonText).jsonObject

                            android.util.Log.d("AddAircraft", "JetAPI raw: $jsonText")

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

                            android.util.Log.d("AddAircraft", "Extracted model: $model")

                            try {
                                supabase.from("posts").insert(
                                    PostInsert(
                                        aircraft_prefix = aircraftPrefix,
                                        aircraft_model = model,
                                        airport_id = airportId,
                                        content = ""
                                    )
                                )

                                Toast.makeText(context, "Aircraft added!", Toast.LENGTH_SHORT)
                                    .show()
                                navController.popBackStack()

                            } catch (dbEx: Exception) {

                                val msg = dbEx.message ?: ""

                                android.util.Log.e("AddAircraft", "Supabase insert error", dbEx)

                                val isDuplicate =
                                    msg.contains("duplicate key value", ignoreCase = true) ||
                                            msg.contains("unique constraint", ignoreCase = true)

                                if (isDuplicate) {
                                    prefixError =
                                        "This aircraft has already been added to Spotted."
                                } else {
                                    prefixError =
                                        "Database insert failed: ${dbEx.message ?: "see log"}"
                                    Toast.makeText(
                                        context,
                                        "DB insert error: ${dbEx.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                        } catch (e: Exception) {
                            android.util.Log.e("AddAircraft", "General error", e)
                            Toast.makeText(
                                context,
                                "Error: ${e.message ?: e}",
                                Toast.LENGTH_LONG
                            ).show()
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
