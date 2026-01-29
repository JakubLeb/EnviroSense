package com.example.envirosense.domain.model

/**
 * Model reprezentujący dane pogodowe
 */
data class Weather(
    val temperature: Double,      // Temperatura w °C
    val feelsLike: Double,        // Temperatura odczuwalna w °C
    val humidity: Int,            // Wilgotność w %
    val pressure: Int,            // Ciśnienie w hPa
    val windSpeed: Double,        // Prędkość wiatru w m/s
    val windDirection: Int,       // Kierunek wiatru w stopniach
    val description: String,      // Opis pogody (np. "zachmurzenie duże")
    val icon: String,             // Kod ikony pogody
    val clouds: Int,              // Zachmurzenie w %
    val visibility: Int,          // Widoczność w metrach
    val sunrise: Long,            // Czas wschodu słońca (Unix timestamp)
    val sunset: Long              // Czas zachodu słońca (Unix timestamp)
) {
    /**
     * Zwraca URL do ikony pogody OpenWeatherMap
     */
    fun getIconUrl(): String = "https://openweathermap.org/img/wn/${icon}@2x.png"
    
    /**
     * Formatuje temperaturę z jednostką
     */
    fun getFormattedTemperature(): String = "${temperature.toInt()}°C"
    
    /**
     * Formatuje prędkość wiatru
     */
    fun getFormattedWindSpeed(): String = "${windSpeed.toInt()} m/s"
    
    /**
     * Formatuje wilgotność
     */
    fun getFormattedHumidity(): String = "$humidity%"
    
    /**
     * Formatuje ciśnienie
     */
    fun getFormattedPressure(): String = "$pressure hPa"
}
