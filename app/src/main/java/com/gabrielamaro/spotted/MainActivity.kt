package com.gabrielamaro.spotted

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import com.gabrielamaro.spotted.navigation.AppNavigation
import com.gabrielamaro.spotted.ui.theme.SpottedTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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
