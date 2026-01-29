package com.example.envirosense.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.envirosense.domain.model.AirQuality
import com.example.envirosense.domain.model.Location
import com.example.envirosense.domain.model.Measurement
import com.example.envirosense.domain.model.Weather

/**
 * Encja Room reprezentująca pomiar zapisany w bazie danych
 */
@Entity(tableName = "measurements")
data class MeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Czas pomiaru
    val timestamp: Long,
    
    // Lokalizacja
    val latitude: Double,
    val longitude: Double,
    val locationName: String?,
    val country: String?,
    
    // Pogoda
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val weatherDescription: String,
    val weatherIcon: String,
    val clouds: Int,
    val visibility: Int,
    val sunrise: Long,
    val sunset: Long,
    
    // Jakość powietrza
    val aqi: Int,
    val pm25: Double,
    val pm10: Double,
    val no2: Double,
    val o3: Double,
    val co: Double,
    val so2: Double,
    val nh3: Double
) {
    /**
     * Konwertuje encję do modelu domenowego
     */
    fun toMeasurement(): Measurement {
        return Measurement(
            id = id,
            timestamp = timestamp,
            location = Location(
                latitude = latitude,
                longitude = longitude,
                name = locationName,
                country = country
            ),
            weather = Weather(
                temperature = temperature,
                feelsLike = feelsLike,
                humidity = humidity,
                pressure = pressure,
                windSpeed = windSpeed,
                windDirection = windDirection,
                description = weatherDescription,
                icon = weatherIcon,
                clouds = clouds,
                visibility = visibility,
                sunrise = sunrise,
                sunset = sunset
            ),
            airQuality = AirQuality(
                aqi = aqi,
                pm25 = pm25,
                pm10 = pm10,
                no2 = no2,
                o3 = o3,
                co = co,
                so2 = so2,
                nh3 = nh3
            )
        )
    }
    
    companion object {
        /**
         * Tworzy encję z modelu domenowego
         */
        fun fromMeasurement(measurement: Measurement): MeasurementEntity {
            return MeasurementEntity(
                id = measurement.id,
                timestamp = measurement.timestamp,
                latitude = measurement.location.latitude,
                longitude = measurement.location.longitude,
                locationName = measurement.location.name,
                country = measurement.location.country,
                temperature = measurement.weather.temperature,
                feelsLike = measurement.weather.feelsLike,
                humidity = measurement.weather.humidity,
                pressure = measurement.weather.pressure,
                windSpeed = measurement.weather.windSpeed,
                windDirection = measurement.weather.windDirection,
                weatherDescription = measurement.weather.description,
                weatherIcon = measurement.weather.icon,
                clouds = measurement.weather.clouds,
                visibility = measurement.weather.visibility,
                sunrise = measurement.weather.sunrise,
                sunset = measurement.weather.sunset,
                aqi = measurement.airQuality.aqi,
                pm25 = measurement.airQuality.pm25,
                pm10 = measurement.airQuality.pm10,
                no2 = measurement.airQuality.no2,
                o3 = measurement.airQuality.o3,
                co = measurement.airQuality.co,
                so2 = measurement.airQuality.so2,
                nh3 = measurement.airQuality.nh3
            )
        }
    }
}
