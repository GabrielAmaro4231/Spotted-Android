package com.gabrielamaro.spotted.ui.home

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun AircraftItem(
    tail: String,
    manufacturer: String,
    model: String,
    airportCity: String,
    airportIcao: String,
    airportIata: String,
    datetime: String,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var thumbnailUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(tail) {
        isLoading = true
        thumbnailUrl = fetchThumbnailForTail(tail)
        isLoading = false
    }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(96.dp)   // wider
                    .height(54.dp)  // 16:9 ratio
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFB3E5FC)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    thumbnailUrl != null -> {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(thumbnailUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Aircraft thumbnail",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                    else -> {
                        Text(text = "✈️", fontSize = 28.sp)
                    }
                }
            }


            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tail,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = model,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            IconButton(onClick = {
                fun enc(s: String) = Uri.encode(s)
                val route = "details/" +
                        enc(tail) + "/" +
                        enc(manufacturer) + "/" +
                        enc(model) + "/" +
                        enc(airportCity) + "/" +
                        enc(airportIcao) + "/" +
                        enc(airportIata) + "/" +
                        enc(datetime)
                navController.navigate(route)
            }) {
                Icon(
                    imageVector = Icons.Filled.Visibility,
                    contentDescription = "View details",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

suspend fun fetchThumbnailForTail(tail: String): String? {
    return withContext(Dispatchers.IO) {
        try {
            val apiUrl = "https://www.jetapi.dev/api?reg=${Uri.encode(tail)}&photos=1&only_jp=true"
            val connection = URL(apiUrl).openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.requestMethod = "GET"

            connection.inputStream.bufferedReader().use { reader ->
                val response = reader.readText()
                val json = JSONObject(response)
                val imagesArray = json.optJSONArray("Images")
                if (imagesArray != null && imagesArray.length() > 0) {
                    val first = imagesArray.getJSONObject(0)
                    val url = first.optString("Thumbnail", null)
                    return@withContext url
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

