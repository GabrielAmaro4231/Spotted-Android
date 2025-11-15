package com.gabrielamaro.spotted.ui.home

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
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
        dateString
    }
}

// ---------------------------------------------
// Home Screen
// ---------------------------------------------
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: com.gabrielamaro.spotted.ui.home.HomeViewModel          // ✅ now using your model file's HomeViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var posts by remember { mutableStateOf<List<FullPost>>(emptyList()) }

    // Load posts
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val postsResult = supabase.from("posts").select(Columns.ALL)
                val rawPosts = postsResult.decodeList<Post>()

                val airportIds = rawPosts.mapNotNull { it.airport_id }.distinct()

                val airports: List<Airport> =
                    if (airportIds.isNotEmpty()) {
                        val airportsJson = supabase.from("airport_list")
                            .select(columns = Columns.ALL) {
                                filter { isIn("id", airportIds) }
                            }
                        airportsJson.decodeList()
                    } else emptyList()

                val airportMap = airports.associateBy { it.id }

                posts = rawPosts.map { post ->
                    FullPost(
                        post = post,
                        airport = airportMap[post.airport_id]
                    )
                }

            } catch (e: Exception) {
                Toast.makeText(context, "Failed to fetch posts: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // UI Layout
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spotted ✈️") },
                actions = { GoogleSignOutButton(navController) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.updateSelectedPost(null)  // NEW AIRCRAFT MODE
                    navController.navigate("addAircraft/new")
                }
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

                PostItem(fullPost) {

                    // 👇 Select post in ViewModel
                    viewModel.updateSelectedPost(fullPost)

                    // 👇 Go to read-only AddAircraft screen
                    navController.navigate("addAircraft/view")
                }
            }
        }
    }
}

// ---------------------------------------------
// Post Card
// ---------------------------------------------
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostItem(fullPost: FullPost, onClick: () -> Unit) {
    val post = fullPost.post
    val airport = fullPost.airport

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                text = "${post.aircraft_prefix ?: "???"} (${post.aircraft_model ?: "unknown"})",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "${airport?.airport_name ?: "Unknown Airport"} " +
                        "(${airport?.airport_icao ?: "----"} - ${airport?.airport_iata ?: "--"})",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = post.aircraft_airline ?: "",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = formatDate(post.created_at),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// ---------------------------------------------
// Sign Out
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
                Toast.makeText(context, "Sign out failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }) {
        Text("Quit")
    }
}
