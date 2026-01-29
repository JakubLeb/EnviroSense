package com.example.envirosense.domain.model

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Model reprezentujący kompletny pomiar środowiskowy
 */
data class Measurement(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val location: Location,
    val weather: Weather,
    val airQuality: AirQuality
) {
    /**
     * Formatuje datę pomiaru
     */
    fun getFormattedDate(): String {
        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp),
            ZoneId.systemDefault()
        )
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    }
    
    /**
     * Formatuje czas pomiaru
     */
    fun getFormattedTime(): String {
        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp),
            ZoneId.systemDefault()
        )
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    
    /**
     * Formatuje pełną datę i czas
     */
    fun getFormattedDateTime(): String {
        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp),
            ZoneId.systemDefault()
        )
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm"))
    }
    
    /**
     * Generuje tekst do udostępnienia
     */
    fun toShareText(): String {
        return """
            📍 EnviroSense - Pomiar środowiskowy
            
            📅 Data: ${getFormattedDateTime()}
            📍 Lokalizacja: ${location.getDisplayName()}
            
            🌡️ POGODA:
            • Temperatura: ${weather.getFormattedTemperature()}
            • Wilgotność: ${weather.getFormattedHumidity()}
            • Wiatr: ${weather.getFormattedWindSpeed()}
            • Ciśnienie: ${weather.getFormattedPressure()}
            • ${weather.description}
            
            💨 JAKOŚĆ POWIETRZA:
            • AQI: ${airQuality.aqi} - ${airQuality.getAqiLevel().label}
            • PM2.5: ${airQuality.getFormattedPm25()}
            • PM10: ${airQuality.getFormattedPm10()}
            
            ${airQuality.getAqiLevel().description}
        """.trimIndent()
    }
}
