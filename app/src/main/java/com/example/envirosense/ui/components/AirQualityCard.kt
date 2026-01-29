package com.example.envirosense.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.envirosense.domain.model.AirQuality

/**
 * Karta wyświetlająca dane o jakości powietrza
 */
@Composable
fun AirQualityCard(
    airQuality: AirQuality,
    modifier: Modifier = Modifier
) {
    val aqiLevel = airQuality.getAqiLevel()
    val aqiColor = airQuality.getAqiColor()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Nagłówek
            Text(
                text = "Jakość powietrza",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // AQI wskaźnik i opis
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // AQI Circle
                AqiIndicator(
                    aqi = airQuality.getAqiValue(),
                    color = aqiColor,
                    modifier = Modifier.size(80.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Opis
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = aqiLevel.emoji,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = aqiLevel.label,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = aqiColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = aqiLevel.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Szczegółowe wartości zanieczyszczeń
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PollutantItem(
                    name = "PM2.5",
                    value = "%.1f".format(airQuality.pm25),
                    unit = "µg/m³"
                )
                PollutantItem(
                    name = "PM10",
                    value = "%.1f".format(airQuality.pm10),
                    unit = "µg/m³"
                )
                PollutantItem(
                    name = "NO₂",
                    value = "%.1f".format(airQuality.no2),
                    unit = "µg/m³"
                )
                PollutantItem(
                    name = "O₃",
                    value = "%.1f".format(airQuality.o3),
                    unit = "µg/m³"
                )
            }
        }
    }
}

/**
 * Wskaźnik AQI w formie koła
 */
@Composable
fun AqiIndicator(
    aqi: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = aqi.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "AQI",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun PollutantItem(
    name: String,
    value: String,
    unit: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontSize = 9.sp
        )
    }
}
