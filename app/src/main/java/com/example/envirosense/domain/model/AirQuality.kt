package com.example.envirosense.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Model reprezentujący dane o jakości powietrza
 */
data class AirQuality(
    val aqi: Int,                 // Air Quality Index (1-5)
    val pm25: Double,             // PM2.5 µg/m³
    val pm10: Double,             // PM10 µg/m³
    val no2: Double,              // NO2 µg/m³
    val o3: Double,               // O3 µg/m³
    val co: Double,               // CO µg/m³
    val so2: Double,              // SO2 µg/m³
    val nh3: Double               // NH3 µg/m³
) {
    /**
     * Poziomy jakości powietrza
     */
    enum class AqiLevel(
        val label: String,
        val description: String,
        val emoji: String
    ) {
        GOOD("Dobra", "Jakość powietrza jest dobra. Idealne warunki do aktywności na zewnątrz.", "🟢"),
        FAIR("Umiarkowana", "Jakość powietrza jest akceptowalna. Osoby wrażliwe mogą odczuwać dyskomfort.", "🟡"),
        MODERATE("Średnia", "Jakość powietrza może wpływać na osoby wrażliwe.", "🟠"),
        POOR("Zła", "Jakość powietrza jest zła. Ogranicz aktywność na zewnątrz.", "🔴"),
        VERY_POOR("Bardzo zła", "Jakość powietrza jest bardzo zła. Unikaj przebywania na zewnątrz.", "🟣")
    }
    
    /**
     * Zwraca poziom jakości powietrza na podstawie AQI
     */
    fun getAqiLevel(): AqiLevel = when (aqi) {
        1 -> AqiLevel.GOOD
        2 -> AqiLevel.FAIR
        3 -> AqiLevel.MODERATE
        4 -> AqiLevel.POOR
        else -> AqiLevel.VERY_POOR
    }
    
    /**
     * Zwraca kolor odpowiadający poziomowi AQI
     */
    fun getAqiColor(): Color = when (aqi) {
        1 -> Color(0xFF4CAF50)    // Zielony
        2 -> Color(0xFF8BC34A)    // Jasnozielony
        3 -> Color(0xFFFFEB3B)    // Żółty
        4 -> Color(0xFFFF9800)    // Pomarańczowy
        else -> Color(0xFFF44336) // Czerwony
    }
    
    /**
     * Oblicza przybliżony indeks AQI w skali 0-500 na podstawie PM2.5
     * (Uproszczona wersja US EPA AQI)
     */
    fun getAqiValue(): Int {
        return when {
            pm25 <= 12.0 -> ((pm25 / 12.0) * 50).toInt()
            pm25 <= 35.4 -> (50 + ((pm25 - 12.1) / 23.3) * 50).toInt()
            pm25 <= 55.4 -> (100 + ((pm25 - 35.5) / 19.9) * 50).toInt()
            pm25 <= 150.4 -> (150 + ((pm25 - 55.5) / 94.9) * 50).toInt()
            pm25 <= 250.4 -> (200 + ((pm25 - 150.5) / 99.9) * 100).toInt()
            else -> (300 + ((pm25 - 250.5) / 249.5) * 200).toInt().coerceAtMost(500)
        }
    }
    
    /**
     * Formatuje wartość PM2.5
     */
    fun getFormattedPm25(): String = "%.1f µg/m³".format(pm25)
    
    /**
     * Formatuje wartość PM10
     */
    fun getFormattedPm10(): String = "%.1f µg/m³".format(pm10)
}
