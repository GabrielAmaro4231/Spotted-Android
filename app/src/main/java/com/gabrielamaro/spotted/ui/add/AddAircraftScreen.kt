package com.gabrielamaro.spotted.ui.add

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.gabrielamaro.spotted.data.supabase
import com.gabrielamaro.spotted.ui.home.HomeViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.InputStream
import java.net.URL

import com.gabrielamaro.spotted.model.Airport
import com.gabrielamaro.spotted.model.PostInsert

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAircraftScreen(navController: NavController, viewModel: HomeViewModel) {

    // Selected post from HomeViewModel (null => create mode)
    val selectedPost = viewModel.selectedPost

    // UI state
    var isEditMode by remember { mutableStateOf(selectedPost == null) } // create -> editable, view -> locked
    var loading by remember { mutableStateOf(false) }

    // Form fields
    var prefix by remember { mutableStateOf("") }                     // never editable when editing an existing post
    var prefixError by remember { mutableStateOf<String?>(null) }

    var airportResults by remember { mutableStateOf<List<Airport>>(emptyList()) }
    var airportMenuExpanded by remember { mutableStateOf(false) }
    var selectedAirport by remember { mutableStateOf<Airport?>(null) }
    var airportSearchText by remember { mutableStateOf("") }
    var searchJob by remember { mutableStateOf<Job?>(null) }

    // Image
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }    // new image picked locally (for create or edit)
    var storedImageUrl by remember { mutableStateOf<String?>(null) }  // existing image url from post

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Initialize with selectedPost when it changes
    LaunchedEffect(selectedPost) {
        val sp = selectedPost
        if (sp != null) {
            // view mode for existing post
            isEditMode = false
            prefix = sp.post.aircraft_prefix ?: ""
            storedImageUrl = sp.post.image_path
            // If we have airport data merged, prefill search text and selectedAirport
            selectedAirport = sp.airport
            airportSearchText = selectedAirport?.let { "${it.airport_name} (${it.airport_icao})" } ?: ""
        } else {
            // create mode
            isEditMode = true
            prefix = ""
            storedImageUrl = null
            selectedAirport = null
            airportSearchText = ""
            selectedImageUri = null
        }
    }

    // Image picker (enabled only in edit/create)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (isEditMode) selectedImageUri = uri
    }

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
                title = { Text(if (selectedPost == null) "Add Aircraft" else "Aircraft Details") },
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

            // Prefix (never editable for existing posts)
            Column {
                OutlinedTextField(
                    value = prefix,
                    onValueChange = {
                        if (selectedPost == null) { // only allow changing when creating new
                            prefix = it.uppercase()
                            prefixError = null
                        }
                    },
                    label = { Text("Aircraft Prefix (e.g. PR-ABC)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = prefixError != null,
                    enabled = selectedPost == null // disable for existing posts
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

            // Airport search dropdown (enabled in edit/create)
            ExposedDropdownMenuBox(
                expanded = airportMenuExpanded,
                onExpandedChange = {
                    if (isEditMode) {
                        airportMenuExpanded = it
                        if (it) performAirportSearch(airportSearchText)
                    }
                }
            ) {

                OutlinedTextField(
                    value = airportSearchText,
                    onValueChange = { text ->
                        if (isEditMode) {
                            airportSearchText = text
                            selectedAirport = null
                        }
                    },
                    label = { Text("Select Airport") },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        if (isEditMode) ExposedDropdownMenuDefaults.TrailingIcon(expanded = airportMenuExpanded)
                    },
                    enabled = isEditMode
                )

                if (isEditMode) {
                    ExposedDropdownMenu(
                        expanded = airportMenuExpanded,
                        onDismissRequest = { airportMenuExpanded = false }
                    ) {
                        if (airportResults.isEmpty()) {
                            DropdownMenuItem(text = { Text("No matching airports") }, onClick = {})
                        } else {
                            airportResults.forEach { airport ->
                                DropdownMenuItem(
                                    text = { Text("${airport.airport_name} (${airport.airport_icao})") },
                                    onClick = {
                                        selectedAirport = airport
                                        airportSearchText = "${airport.airport_name} (${airport.airport_icao})"
                                        airportMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Image area
            if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            } else if (storedImageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(storedImageUrl),
                    contentDescription = "Stored Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            // Change image button (only in edit/create)
            if (isEditMode) {
                Button(onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                    Text("Pick Photo")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            if (selectedPost == null) {
                // CREATE MODE
                Button(
                    onClick = {
                        // create new post
                        val pfx = prefix.trim()
                        val airportId = selectedAirport?.id ?: run {
                            Toast.makeText(context, "Please select an airport", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        loading = true
                        prefixError = null

                        scope.launch {
                            try {
                                // JetAPI lookup for model/airline
                                val url = "https://www.jetapi.dev/api?reg=$pfx&photos=0&flights=0"

                                val jsonText = withContext(kotlinx.coroutines.Dispatchers.IO) {
                                    URL(url).readText()
                                }

                                val json = Json.parseToJsonElement(jsonText).jsonObject
                                val flightRadarElement = json["FlightRadar"]

                                if (flightRadarElement == null ||
                                    flightRadarElement.toString() == "null" ||
                                    flightRadarElement.toString() == "[]"
                                ) {
                                    prefixError = "No aircraft found with this prefix."
                                    loading = false
                                    return@launch
                                }

                                val model = flightRadarElement.jsonObject["Aircraft"]?.jsonPrimitive?.content
                                val airline = flightRadarElement.jsonObject["Airline"]?.jsonPrimitive?.content

                                if (model.isNullOrBlank()) {
                                    prefixError = "No aircraft model information available."
                                    loading = false
                                    return@launch
                                }

                                var uploadedImagePath: String? = null

                                if (selectedImageUri != null) {
                                    try {
                                        val inputStream: InputStream? = context.contentResolver.openInputStream(selectedImageUri!!)
                                        val bytes = inputStream?.readBytes()

                                        if (bytes != null) {
                                            val filename = "aircraft_${System.currentTimeMillis()}.jpg"
                                            val folder = "aircraft_photos"
                                            val path = "$folder/$filename"

                                            supabase.storage.from("aircraft-photos").upload(path, bytes)

                                            val projectId = supabase.supabaseUrl.substringAfter("https://")
                                                .substringBefore(".supabase.co")

                                            uploadedImagePath =
                                                "https://$projectId.supabase.co/storage/v1/object/public/aircraft-photos/$path"
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                // Insert post
                                supabase.from("posts").insert(
                                    PostInsert(
                                        aircraft_prefix = pfx,
                                        aircraft_model = model,
                                        aircraft_airline = airline ?: "",
                                        airport_id = airportId,
                                        content = "",
                                        image_path = uploadedImagePath
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
            } else {
                // EXISTING post -> view mode (with Edit/Delete)
                if (!isEditMode) {
                    // VIEW-ONLY: Edit + Delete
                    Button(onClick = { isEditMode = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Edit")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            // Delete post + image
                            val postId = selectedPost.post.id
                            if (postId == null) {
                                Toast.makeText(context, "Unable to delete: invalid post id", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            scope.launch {
                                try {
                                    // delete post row
                                    supabase.from("posts").delete {
                                        filter { eq("id", postId) }
                                    }

                                    // delete image from storage if exists
                                    val url = selectedPost.post.image_path
                                    if (!url.isNullOrBlank()) {
                                        val key = url.substringAfter("/object/public/aircraft-photos/")
                                        try {
                                            supabase.storage.from("aircraft-photos").delete(key)
                                        } catch (_: Exception) { /* ignore delete error */ }
                                    }

                                    Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error deleting: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete Post", color = MaterialTheme.colorScheme.onError)
                    }
                } else {
                    // EDIT MODE for existing post: allow changing airport and image (prefix remains locked)
                    Button(
                        onClick = {
                            val postId = selectedPost.post.id
                            if (postId == null) {
                                Toast.makeText(context, "Unable to update: invalid post id", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val airportId = selectedAirport?.id ?: run {
                                Toast.makeText(context, "Please select an airport", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            loading = true

                            scope.launch {
                                try {
                                    var uploadedImagePath: String? = selectedPost.post.image_path

                                    // If user picked new image, upload and replace path
                                    if (selectedImageUri != null) {
                                        try {
                                            val inputStream: InputStream? = context.contentResolver.openInputStream(selectedImageUri!!)
                                            val bytes = inputStream?.readBytes()

                                            if (bytes != null) {
                                                val filename = "aircraft_${System.currentTimeMillis()}.jpg"
                                                val folder = "aircraft_photos"
                                                val path = "$folder/$filename"

                                                supabase.storage.from("aircraft-photos").upload(path, bytes)

                                                val projectId = supabase.supabaseUrl.substringAfter("https://")
                                                    .substringBefore(".supabase.co")

                                                uploadedImagePath =
                                                    "https://$projectId.supabase.co/storage/v1/object/public/aircraft-photos/$path"

                                                // try deleting old image (best-effort)
                                                val oldUrl = selectedPost.post.image_path
                                                if (!oldUrl.isNullOrBlank()) {
                                                    val oldKey = oldUrl.substringAfter("/object/public/aircraft-photos/")
                                                    try {
                                                        supabase.storage.from("aircraft-photos").delete(oldKey)
                                                    } catch (_: Exception) { /* ignore */ }
                                                }
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    // Update DB row: airport_id and image_path (leave other fields untouched)
                                    supabase.from("posts").update({
                                        set("airport_id", airportId)
                                        set("image_path", uploadedImagePath)
                                    }) {
                                        filter { eq("id", postId) }
                                    }

                                    Toast.makeText(context, "Post updated", Toast.LENGTH_SHORT).show()
                                    // exit edit mode, refresh local state
                                    isEditMode = false
                                    // update stored image url so UI shows new image
                                    storedImageUrl = uploadedImagePath
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    loading = false
                                }
                            }
                        },
                        enabled = !loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (loading) "Saving..." else "Save Changes")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        // Cancel edits: revert selectedImageUri and airport selection to stored values
                        selectedImageUri = null
                        selectedAirport = selectedPost.airport
                        airportSearchText = selectedAirport?.let { "${it.airport_name} (${it.airport_icao})" } ?: ""
                        isEditMode = false
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
