package com.example.envirosense.data.remote

import com.example.envirosense.data.remote.dto.AirQualityResponse
import com.example.envirosense.data.remote.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfejs Retrofit dla OpenWeatherMap API
 */
interface OpenWeatherApiService {
    
    /**
     * Pobiera aktualne dane pogodowe dla podanych współrzędnych
     * 
     * @param lat Szerokość geograficzna
     * @param lon Długość geograficzna
     * @param apiKey Klucz API OpenWeatherMap
     * @param units Jednostki (metric = Celsjusz)
     * @param lang Język odpowiedzi (pl = polski)
     */
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pl"
    ): WeatherResponse
    
    /**
     * Pobiera dane o jakości powietrza dla podanych współrzędnych
     * 
     * @param lat Szerokość geograficzna
     * @param lon Długość geograficzna
     * @param apiKey Klucz API OpenWeatherMap
     */
    @GET("data/2.5/air_pollution")
    suspend fun getAirQuality(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): AirQualityResponse
    
    companion object {
        const val BASE_URL = "https://api.openweathermap.org/"
    }
}
