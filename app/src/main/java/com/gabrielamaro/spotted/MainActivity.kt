package com.gabrielamaro.spotted

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.gabrielamaro.spotted.navigation.AppNavigation
import com.gabrielamaro.spotted.ui.theme.SpottedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpottedTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}
