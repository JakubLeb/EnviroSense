package com.example.envirosense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.envirosense.data.local.EnviroDatabase
import com.example.envirosense.ui.navigation.EnviroNavigation
import com.example.envirosense.ui.theme.EnviroSenseTheme
import com.example.envirosense.util.LocationHelper

/**
 * Główna aktywność aplikacji EnviroSense
 */
class MainActivity : ComponentActivity() {
    
    private lateinit var database: EnviroDatabase
    private lateinit var locationHelper: LocationHelper
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicjalizacja zależności
        database = EnviroDatabase.getInstance(applicationContext)
        locationHelper = LocationHelper(applicationContext)
        
        enableEdgeToEdge()
        
        setContent {
            EnviroSenseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EnviroNavigation(
                        database = database,
                        locationHelper = locationHelper
                    )
                }
            }
        }
    }
}
