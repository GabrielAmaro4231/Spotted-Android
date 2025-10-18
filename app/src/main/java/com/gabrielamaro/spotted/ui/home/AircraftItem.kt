package com.gabrielamaro.spotted.ui.home

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

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
            // Left-side square image placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFB3E5FC)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✈️", fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Tail number and model
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

            // Eye icon: encode args and navigate
            IconButton(onClick = {
                // Use android.net.Uri.encode to encode each segment
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
