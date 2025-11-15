package com.gabrielamaro.spotted.ui.home

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gabrielamaro.spotted.data.supabase
import com.gabrielamaro.spotted.model.Airport
import com.gabrielamaro.spotted.model.Post
import com.gabrielamaro.spotted.model.FullPost
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

// ---------------------------------------------
// Date formatting helper
// ---------------------------------------------
@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(dateString: String?): String {
    if (dateString.isNullOrBlank()) return "No date"

    return try {
        val parsed = OffsetDateTime.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")
        parsed.format(formatter)
    } catch (e: Exception) {
        dateString // fallback
    }
}

// ---------------------------------------------
// Home Screen
// ---------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var posts by remember { mutableStateOf<List<FullPost>>(emptyList()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                Log.d("HOME_DEBUG", "Fetching posts...")

                // 1. Fetch posts (explicitly request all columns)
                val postsResult = supabase.from("posts").select(Columns.ALL)
                val rawPosts = postsResult.decodeList<Post>()

                Log.d("HOME_DEBUG", "POSTS FETCHED (${rawPosts.size}):")
                rawPosts.forEach {
                    Log.d(
                        "HOME_DEBUG",
                        "POST id=${it.id}, airport_id=${it.airport_id}, prefix=${it.aircraft_prefix}"
                    )
                }

                // Collect distinct airport IDs referenced by posts
                val airportIds: List<Int> = rawPosts
                    .mapNotNull { it.airport_id }
                    .distinct()

                Log.d("HOME_DEBUG", "Distinct airport IDs referenced: $airportIds")

                val airports: List<Airport> = if (airportIds.isNotEmpty()) {
                    Log.d("HOME_DEBUG", "Fetching only referenced airports via IN query...")

                    // Fetch only the airports matching the post airport_ids
                    val airportsJson = supabase.from("airport_list")
                        .select(columns = Columns.ALL) {
                            filter {
                                isIn("id", airportIds)
                            }
                        }

                    // Decode
                    val decoded = airportsJson.decodeList<Airport>()

                    // Debug logs
                    Log.d("HOME_DEBUG", "AIRPORTS FETCHED (${decoded.size}):")
                    decoded.forEach { a ->
                        Log.d(
                            "HOME_DEBUG",
                            "AIRPORT id=${a.id}, icao=${a.airport_icao}, name=${a.airport_name}"
                        )
                    }

                    decoded // <-- THIS is the return value of the IF block

                } else {
                    Log.d("HOME_DEBUG", "No airport IDs found in posts; skipping airport query.")
                    emptyList()
                }

                // 3. Create lookup map
                val airportMap = airports.associateBy { it.id }

                Log.d("HOME_DEBUG", "Merging posts with airport data...")

                // 4. Merge posts + airport data
                posts = rawPosts.map { post ->
                    val airport = airportMap[post.airport_id]

                    Log.d(
                        "HOME_DEBUG",
                        "MATCH post_id=${post.id} airport_id=${post.airport_id} -> airport_found=${airport != null}"
                    )

                    FullPost(
                        post = post,
                        airport = airport
                    )
                }

                Log.d("HOME_DEBUG", "Merge complete. Total merged posts: ${posts.size}")

            } catch (e: Exception) {
                Log.e("HomeScreen", "Fetch error", e)
                Toast.makeText(context, "Failed to fetch posts: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spotted ✈️") },
                actions = { GoogleSignOutButton(navController) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addAircraft") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Aircraft")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            items(posts) { fullPost ->
                PostItem(fullPost)
            }
        }
    }
}

// ---------------------------------------------
// UI for each post
// ---------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostItem(fullPost: FullPost) {
    val post = fullPost.post
    val airport = fullPost.airport

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            // Line 1 — Aircraft
            Text(
                text = "${post.aircraft_prefix ?: "???"} (${post.aircraft_model ?: "unknown"})",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Line 2 — Airport
            Text(
                text = "${airport?.airport_name ?: "Unknown Airport"} " +
                        "(${airport?.airport_icao ?: "----"} - ${airport?.airport_iata ?: "--"})",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Line 3 — Airline
            Text(
                text = post.aircraft_airline ?: "",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Line 4 — Formatted date
            Text(
                text = formatDate(post.created_at),
                style = MaterialTheme.typography.bodySmall
            )

        }
    }
}

// ---------------------------------------------
// Sign out button
// ---------------------------------------------

@Composable
fun GoogleSignOutButton(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Button(onClick = {
        coroutineScope.launch {
            try {
                supabase.auth.signOut()
                Toast.makeText(context, "Signed out successfully!", Toast.LENGTH_SHORT).show()

                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }

            } catch (e: Exception) {
                Log.e("GoogleSignOut", "SignOut error", e)
                Toast.makeText(context, "Sign out failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }) {
        Text("Quit")
    }
}
