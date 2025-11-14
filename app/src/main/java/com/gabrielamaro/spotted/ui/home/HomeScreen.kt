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
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
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
// Data classes
// ---------------------------------------------

@Serializable
data class Post(
    val id: Long? = null,
    val created_at: String? = null,
    val content: String? = null,
    val user_id: String? = null,

    val aircraft_prefix: String? = null,
    val aircraft_model: String? = null,

    val airport_id: Long? = null
)

@Serializable
data class Airport(
    val id: Long? = null,
    val airport_name: String? = null,
    val airport_icao: String? = null,
    val airport_iata: String? = null
)

data class FullPost(
    val post: Post,
    val airport: Airport?
)

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

    // Fetch both tables at once, then merge
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                // 1. Fetch all posts
                val postsResult = supabase.from("posts").select()
                val rawPosts = postsResult.decodeList<Post>()

                // 2. Fetch all airports
                val airportsResult = supabase.from("airport_list").select()
                val airports = airportsResult.decodeList<Airport>()

                // 3. Create lookup map
                val airportMap = airports.associateBy { it.id }

                // 4. Merge
                posts = rawPosts.map { post ->
                    val airport = airportMap[post.airport_id]
                    FullPost(post, airport)
                }

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

            // Line 3 — Formatted date
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
