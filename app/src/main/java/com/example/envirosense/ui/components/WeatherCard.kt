package com.example.envirosense.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Compress
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.envirosense.domain.model.Weather

/**
 * Karta wyświetlająca dane pogodowe
 */
@Composable
fun WeatherCard(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Nagłówek - temperatura i ikona
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = weather.getFormattedTemperature(),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = weather.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                
                AsyncImage(
                    model = weather.getIconUrl(),
                    contentDescription = weather.description,
                    modifier = Modifier.size(80.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Szczegóły pogody
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem(
                    icon = { 
                        Icon(
                            imageVector = Icons.Outlined.WaterDrop,
                            contentDescription = "Wilgotność",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    label = "Wilgotność",
                    value = weather.getFormattedHumidity()
                )
                
                WeatherDetailItem(
                    icon = { 
                        Icon(
                            imageVector = Icons.Outlined.Air,
                            contentDescription = "Wiatr",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    label = "Wiatr",
                    value = weather.getFormattedWindSpeed()
                )
                
                WeatherDetailItem(
                    icon = { 
                        Icon(
                            imageVector = Icons.Outlined.Compress,
                            contentDescription = "Ciśnienie",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    label = "Ciśnienie",
                    value = weather.getFormattedPressure()
                )
            }
        }
    }
}

@Composable
private fun WeatherDetailItem(
    icon: @Composable () -> Unit,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon()
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            fontSize = 10.sp
        )
    }
}
