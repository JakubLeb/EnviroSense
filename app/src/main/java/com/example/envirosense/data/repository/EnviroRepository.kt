package com.example.envirosense.data.repository

import com.example.envirosense.BuildConfig
import com.example.envirosense.data.local.MeasurementDao
import com.example.envirosense.data.local.MeasurementEntity
import com.example.envirosense.data.remote.OpenWeatherApiService
import com.example.envirosense.domain.model.AirQuality
import com.example.envirosense.domain.model.Location
import com.example.envirosense.domain.model.Measurement
import com.example.envirosense.domain.model.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repozytorium zarządzające danymi środowiskowymi
 */
class EnviroRepository(
    private val measurementDao: MeasurementDao,
    private val weatherApiService: OpenWeatherApiService
) {
    // Klucz API - pobierany z BuildConfig
    private val apiKey: String = BuildConfig.OPENWEATHER_API_KEY
    
    // ==================== API CALLS ====================
    
    /**
     * Pobiera dane pogodowe z API
     */
    suspend fun fetchWeather(latitude: Double, longitude: Double): Result<Pair<Weather, Location>> {
        return try {
            val response = weatherApiService.getWeather(
                lat = latitude,
                lon = longitude,
                apiKey = apiKey
            )
            Result.success(Pair(response.toWeather(), response.toLocation()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Pobiera dane o jakości powietrza z API
     */
    suspend fun fetchAirQuality(latitude: Double, longitude: Double): Result<AirQuality> {
        return try {
            val response = weatherApiService.getAirQuality(
                lat = latitude,
                lon = longitude,
                apiKey = apiKey
            )
            Result.success(response.toAirQuality())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Pobiera wszystkie dane środowiskowe (pogoda + jakość powietrza)
     */
    suspend fun fetchEnvironmentData(latitude: Double, longitude: Double): Result<Measurement> {
        return try {
            // Pobierz pogodę
            val weatherResult = fetchWeather(latitude, longitude)
            if (weatherResult.isFailure) {
                return Result.failure(weatherResult.exceptionOrNull() ?: Exception("Failed to fetch weather"))
            }
            val (weather, location) = weatherResult.getOrThrow()
            
            // Pobierz jakość powietrza
            val airQualityResult = fetchAirQuality(latitude, longitude)
            if (airQualityResult.isFailure) {
                return Result.failure(airQualityResult.exceptionOrNull() ?: Exception("Failed to fetch air quality"))
            }
            val airQuality = airQualityResult.getOrThrow()
            
            // Złóż kompletny pomiar
            val measurement = Measurement(
                timestamp = System.currentTimeMillis(),
                location = location,
                weather = weather,
                airQuality = airQuality
            )
            
            Result.success(measurement)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== DATABASE OPERATIONS ====================
    
    /**
     * Pobiera wszystkie zapisane pomiary
     */
    fun getAllMeasurements(): Flow<List<Measurement>> {
        return measurementDao.getAllMeasurements().map { entities ->
            entities.map { it.toMeasurement() }
        }
    }
    
    /**
     * Pobiera ostatnie N pomiarów
     */
    fun getRecentMeasurements(limit: Int): Flow<List<Measurement>> {
        return measurementDao.getRecentMeasurements(limit).map { entities ->
            entities.map { it.toMeasurement() }
        }
    }
    
    /**
     * Pobiera pomiar po ID
     */
    suspend fun getMeasurementById(id: Long): Measurement? {
        return measurementDao.getMeasurementById(id)?.toMeasurement()
    }
    
    /**
     * Zapisuje pomiar do bazy danych
     */
    suspend fun saveMeasurement(measurement: Measurement): Long {
        val entity = MeasurementEntity.fromMeasurement(measurement)
        return measurementDao.insertMeasurement(entity)
    }
    
    /**
     * Usuwa pomiar
     */
    suspend fun deleteMeasurement(measurement: Measurement) {
        val entity = MeasurementEntity.fromMeasurement(measurement)
        measurementDao.deleteMeasurement(entity)
    }
    
    /**
     * Usuwa pomiar po ID
     */
    suspend fun deleteMeasurementById(id: Long) {
        measurementDao.deleteMeasurementById(id)
    }
    
    /**
     * Usuwa wszystkie pomiary
     */
    suspend fun deleteAllMeasurements() {
        measurementDao.deleteAllMeasurements()
    }
    
    /**
     * Pobiera liczbę pomiarów
     */
    suspend fun getMeasurementCount(): Int {
        return measurementDao.getMeasurementCount()
    }
}
